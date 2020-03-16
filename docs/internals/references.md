# Introduction

What is a reference? A reference is special kind of IR vertex that points to another piece of information somewhere.
This 'pointer' may or may not be concrete, i.e. a reference can be in multiple states: not resolved yet, unresolved,
and resolved.

# Different types of references

## Full reference

A reference that is 'full' contains a known namespace and a known name part. This basically means that such a reference
is context-free.

Full references are either specified with an explicit namespace (`<namespace>:<path>`), or an implicit namespace
(`:<name>`). The value of the implicit namespace is equivalent to the namespace of the file in which such a reference is
declared. A reference with an implicit part can be considered full, since the implicit namespace is already known at
parse time.

Examples: `minecraft:tick`, `:a\b`.

## Partial reference

References that are not context-free, i.e. non-full references, will depend on the context in which they are declared in
order to be resolved unambiguously.

An example of a partial reference is a reference to a local variable.
Consider this piece of MotorScript code:

```motorscript
func example -> Int
{
    var a = 1
    return ++a
}
```

The expression `++a` references the local variable `a`. There is simply only one character, namely _a_ that indicates
the reference. Since any identifier will be transformed into a reference, simply a non-keyword word is also a reference.
The point here is that there is no namespace associated with the reference (there is no colon nor text preceding a
colon) in this identifier. This is thus processed as a partial reference. The only known thing is that the concrete
reference ends with 'a'. Variables, and especially local variables, can't really be expressed as a full reference
anyways, as we'll see later. However, at this point it is still a partial reference and the piece 'a' can still refer to
anything, since it's not immediately known that this 'a' is meant to point to the local variable from the third line.

To give another example that involves invocation of a function, consider the following piece of MotorScript code:
```motorscript
use text\tellraw
user func hello
{
    tellraw(@s, "Hello!")
}
```

This piece of code actually has two references in it, namely `text\tellraw` (line 1) and `tellraw` (line 4).
Both references are processed in a different way (we'll get to that), but in the end they are both just plain partial
references.

The first reference exists of two path elements, `text` followed by `tellraw`. This means that it's referring to the
`tellraw` item that is a direct child of an item with the name `text`. By the rules for resolving import references,
this will be resolved to `minecraft:text\tellraw` (assuming no local override exist).

The second reference is just referring to something ending in the name `tellraw`. This is obviously pointing to the item
that was imported with the use-statement in line 1, but if that were not there, it might also just refer to a function
declared in the same file that happens to be called `tellraw`.

# Reference sources

There are quite a lot of place in which a reference can be declared. The following sub-sections cover all possible
places in which a reference is declared.

## Top-level declarations

Top-level declarations is one of the most obvious sources of reference declaration. It's simply all the functions and
containers that are declared at the top-level of a file. Something is at 'top-level' in a file when there exists no
wrapping scope for the item. Generally this just means that, if properly formatted, there is no indentation in front of
such an item.

From inside any function body, all top-level items in the same file are accessible. This is what one would expect.
The possibility to refer from inside a top-level expression to other top-level containers is limited. From such an
expression only top-level containers that were declared 'above' are reachable.
Cycles can be created if this were not the case. This is also why non-builtin functions are not available in top-level
expressions.

Reachability of a top-level item from other places depends on its access level. See (TODO) for more information about
access levels and more about modifiers in general.

One important modifier that's relevant to here is the `default` modifier. This modifier can be given to at most one
top-level item. This top-level item should be identified by the same name as the file in which it resides minus the
`.mos` extension. The item with this modifier will be exported under the full reference of the current file (whereas all
other non-private top-level items would be exported as a sub-item under the full reference of the file in which it is
declared).

## Local declarations

Any scope can have local declarations. These are (currently) limited to containers only.
Such containers can, as one would expect, only be reached after their initial declaration.

A locally declared container is available in all remaining statements in the scope, and it's available in sub-scopes.

## Standard library

There's not much to say about the standard library. It simply provides some references, depending on the targeted
platform and version of that platform. Items from the standard library are available in all places, they do still need
to be imported though.

## Dependencies

Practically identical to the standard library, since the standard library is also just a dependency (albeit an implied
one if not specified explicitly).

## Build configuration

**NOTE**: This is a planned feature.

The build configuration may contain a section that declares certain tags and resources.

# Reference meta data

As seen above, references pop up in a lot of places. But as described, not all references are reachable from all places.
This section tries to make clear how this differentiation is dealt with internally. It's a step up to the section about
resolving partial references.

During IR construction, a certain "reference context" is being kept track of. For more in-depth details about the IR
construction and different contexes, see the documentation about the ["front" compiler step](./organization/front.md).

Now, there are certain constrains for different types of references. This is mostly where the reference differentiation
comes from. This is a list of these constraints:

- References directly from a resource or tag don't use the local file scope, but the global scope. This is because it's
  (currently) not possible to define custom resources or define tags directly from a source file. This is also because
  it makes it possible to have a variable name that has the same name as, for example, a certain predefined entity.
- Top-level expressions can only refer to items with the `builtin` modifier (mostly concerning functions). This is to
  prevent reference cycles (for example a top-level expression refers to a function that contains a reference to the
  variable that would have the value assigned that would be returned by that function).
- Partial references from whithin `use` statements don't follow the general rules for resolving partial references, but
  have their own rules. What these rules exactly are, is described in the next subsection.

These three different kinds reference are internally recogized by (TODO).

TODO: `text\tellraw(@s, "Hello")` zou moeten werken, ook al heb je niet expliciet dat ge&iuml;mporteerd

# Resolving of partial references

Only partial references need resolving at all. However, the thing is that in practice most references are actually
partial. Since we now know that not all partial references are the same, different variations require different
treatment. However, the general idea behind this resolving is common to all of them.

## From within a `use` statement

References from within `use` statements are an exception. As stated earlier, they have their own rules. This may not be
that surprising, because in the end these references in turn will decide to what other references in the file may refer
to.

The set of possible items such a reference can refer to, consists of all public items from all dependencies (so, that
also includes the standard library), but also all public _and_ internal items from the current project.

The list of internal items is constructed after the IR has been generated. After successfull IR generation, it is known
which items will be made available, but they're all still missing type information. This type information will be
inferred and known later on.

Now, since we're talking about partial references, one of the tasks is to figure out which namespace is going to be used
in the concrete reference. And that really depend on what the user points to. Consider the following statement:

```motorscript
use text\say
```

Now, from experience you might conclude that this is meant to point to `minecraft:text\tellraw`. But you wouldn't be
sure about that without taking information about the context into consideration. Because, what happens when there is an
item in the current project that is relative to the file in which this statement was written, that happens to have the
same name? Will that make a clash, or will that make the resolving impossible, or is that behavior not defined?
Well, (TODO).

## Other variations

...

TODO: from imports: `prelude`, `minecraft`, `<src file ns>`. maar, boven deze namespace-prioriteiten is de relatieve import
  belangrijker. daarom kun je dus bijvoorbeeld `use text\tellraw` overriden als je relatief t.o.v. het bronbestand een
  item in de namespace hebt dat eindigt met `text\tellraw`.

# What and Why?

What's a naming convention and why is it necessary? What's the point?

Let's assume somebody has written a very cool piece of software in MotorScript and wants others to integrate with it.
Perhaps it's some kind of minigame library that manages turns of players, keeps track of scores, etc.
Now, how would it be possible for others to integrate with this library? It would be very convenient if MotorScript
emitted all its generated functions in a predictable way. And that's exactly what this naming convention is for.

The current naming scheme is not based on anything in particular, but is just almost the first thing I came up with.
And it's not only external parties that may benefit from this naming convention, but the compiler itself also heavily
relies on this standard. All internal function calls are performed to function names created according to this naming
convention too.

Note: we use the forward slash notation here, since this is closer related to actual data packs than it is to internals
of the compiler (where we would use backwards slashes to separate parts of the name of a namespaced ID).

# TL;DR

```text
sha(x) = base32(SHA-256(x)[:20])
H(nsid, type) = sha(nsid + '|' + type)
published_name_prefix(nsid, type) = "zzz__mos:" + nsid.namespace + '/' + H(nsid, type)
```

# Base namespace

Since everything is _always_ part of a namespace, you might expect that these items would also be put into that
namespace. The problem with doing this is that it would clutter the autocomplete results in the in-game command boxes
(e.g. the chat bar, or the command block configuration window). Ideally, we wouldn't want the internal function names to
show up in these autocomplete results, but that's impossible (at least currently AFAIK). So, the only solution to push
down everything as far as possible is to give it a name with a lot of letters z in the beginning. That is the reason
that not everything is contained in the namespace in which it actually belongs, but in a 'shared' namespace that will
try to hide as much elements as possible.

This common namespace, or base namespace is `zzz__mos`. This means that everything will be prefixed with `zzz__mos:`.

# Unique element identifier

Every element will be made available uniquely by means of a hash. This hash is a SHA-256 (SHA-2) hash converted into a
base32 (lowercase) string. This hash is computed by applying the concatenation by pipe of the full name of the element
and the type of the element to the hash function. Only the upper 20 bytes of the hash are used. This makes the final
output exactly 32 characters long, which is very unique while not being too long.
Note that the full name of the element is the NSID of the element, using backwards slashes.
Demonstrating pseudo code:

```text
sha(x) = base32(SHA-256(x)[:20])
H(nsid, type) = sha(nsid + '|' + type)
H("myns:example\elem", "function") = sha("myns:example\elem|function")
                                   = "giztqnbrgfqtkyjqhe4wen3fmuywemrr"
```

The base name and the unique element identifier make up the published name prefix. Note that the namespace will be
prepended to the hash.
For this example, that would be `zzz__mos:myns/giztqnbrgfqtkyjqhe4wen3fmuywemrr`.

# Functions split up

Typically a function is more than one very simple statement. Sometimes there are loops, branches, etc. that need to be
compiled. Internally a function as one unit will be compiled down to multiple `.mcfunction` files, simply to make
conditional execution (branching, looping) possible. But still, those separate function files are part of the same
function in the original source. That's why the published name prefix is a prefix: it's reused for parts that are nested
inside the element.

Each element can theoretically have unlimited nested elements, but MotorScript keeps it only at the first level. The
name of the nested element is simply concatenated to the published name prefix.

For 3rd party usage, there is only one of such nested elements of particular interest, namely the entry point of the
function. Per convention this will be available using the nested element `ep`.
Elaborating the previous example, this will in turn become: `zzz__mos:myns/giztqnbrgfqtkyjqhe4wen3fmuywemrr/ep`.

Of course the entry point is not the only nested element. MotorScript emits way more of these functions. Even though
they do follow some convention, they are not intended to be used or accessible by third parties. Even patch versions of
the compiler may change the naming scheme of these elements.

[//]: <> (Maybe give a list of currently internally used elements)

# Special cases

In the code, some items can have extra modifiers. A function for example can be `private`, `internal` or `user`.

Elements that have those certain modifiers will have their names be computed differently. Both `private` and `internal`
elements are only visible to the project they're being compiled from, so they are never intended to be used publicly.
However, they will not be treated specially when it comes to generating these identifiers. They only affect the way the
external definitions are generated.

The single exception is currently the `user` modifier. This modifier can be placed onto a function and means that the
author intends this function to be callable by the user (player). Now it's of course not realistic to let the user enter
the mangled name. So what this modifier will do, is emit an extra `.mcfunction` file that simply immediately calls the
function's entry point by the published name.

To demonstrate this, assume the previously used `myns:example\elem` is a function with the `user` modifier. Then the
compiler will also create the file `elem.mcfunction` in the data pack directory `data/myns/functions/example`, with the
following content:

```mcfunction
function zzz__mos:myns/giztqnbrgfqtkyjqhe4wen3fmuywemrr/ep
```

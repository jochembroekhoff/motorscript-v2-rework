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
sha(x) = hexstring(SHA-244(x)[:14])
H(nsid, type) = sha(nsid + '|' + type)
published_name_prefix(nsid, type) = "zzz_mos:" + H(nsid, type)
```

# Base namespace

Since everything is _always_ part of a namespace, you might expect that these items would also be put into that
namespace. TODO: explain problem with autocomplete.

OLD:
What follows after a namespace is always a list of name parts, but in this case that name is special. And it is:
`zzz__mos`. There is some reasoning behind this, although that's not particularly clever. The thing is that we don't
want these generated entries to show up in the in-game command autocomplete box. But since that's impossible (at least
currently AFAIK), this is _the_ way to get stuff as far down in that list as possible, because that list is sorted
alphabetically.

So what we end up with, is a base name of `<namespace>:zzz_mos`.

# Unique element identifier

Every element will be made available uniquely by means of a hash. This hash is a SHA-224 (SHA-2) hash converted into a
hex string. This hash is computed by applying the concatenation by pipe of the full name of the element and the type of
the element to the hash function. Only the first half of the hash is used (upper 14 bytes).
Note that the full name of the element is the NSID of the element, using backwards slashes.
Demonstrating pseudo code:

```text
sha(x) = hexstring(SHA-244(x)[:14])
H(nsid, type) = sha(nsid + '|' + type)
H("myns:example", "function") = sha("myns:example\element|function")
                              = "b2d838ab6aa9de0c89968acb38e8"
```

# Function's entry point

TODO: `<base NSID>/ep`

# Functions split up

TODO: some generated functions are internal to that function and are not intended to be of 3rd party interest.
      there's no guarantee that these names stay stable across MotorScript versions

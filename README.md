# Portable Text for Clojure

A Clojure library for rendering the
[PortableText](https://github.com/portabletext/portabletext) rich text format
content (used in [Sanity](https://sanity.io)) to hiccup or HTML.

Passes most of the official
[tests](https://github.com/sanity-io/block-content-tests), except for some
JavaScript library-specific features, and some image transforms (to be
implemented).

## Install

With tools.deps:

```clj
cjohansen/portable-text {:mvn/version "2020.02.12"}
```

With Leiningen:

```clj
[cjohansen/portable-text "2020.02.12"]
```

## Usage

```clj
(require '[portable-text.html :as pt])

(def content-blocks
  [{:_key "R5FvMrjo"
    :_type "block"
    :children
    [{:_key "cZUQGmh4"
      :_type "span"
      :marks []
      :text "Plain text."}]
    :markDefs []
    :style "normal"}])

(pt/render content-blocks)
;;=> "<p>Plain text.</p>"

(pt/to-hiccup content-blocks)
;;=> [:p {} ("Plain text.")]
```

You can optionally pass an options map to both `to-hiccup` and `render` to
specify details about your Sanity installation, which is necessary to render
images correctly:

```clj
(pt/render
 [{:_type "image",
   :_key "d234a4fa317a",
   :asset
   {:_type "reference",
    :_ref "image-YiOKD0O6AdjKPaK24WtbOEv0-3456x2304-jpg"}}]
 {:sanity/project-id "3do82whm"
  :sanity/dataset "production"})

;;=> "<figure><img src=\"https://cdn.sanity.io/images/3do82whm/production/YiOKD0O6AdjKPaK24WtbOEv0-3456x2304.jpg\"/></figure>"
```

### Images

Images are rendered from Sanity's CDN by default, but you can override it:

```clj
(sut/to-hiccup
 [{:_type "image",
   :_key "d234a4fa317a",
   :asset
   {:_type "reference",
    :_ref "image-yoda-3456x2304-jpg"}}]
 {:sanity/project-id "abcd"
  :sanity/dataset "prod"
  :cdn-url "https://cdn.mysite.com"})

;;=> [:figure {}
;;    [:img
;;     {:src "https://cdn.mysite.com/images/abcd/prod/yoda-3456x2304.jpg"}]]
```

Image transforms such as crop and hotspot are not (yet) supported.

## Tests

```sh
make test # ...or
make autotest
```

## License

Copyright © 2020 Christian Johansen

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.

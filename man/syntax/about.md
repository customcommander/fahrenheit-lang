The `about` map provides information about your citation and bibliographic style:

```clojure
{:about
  {:id "https://example.com/styles/foo"
   :title "foo"     ; can be localised
   :title-short "f" ; can be localised
   }}
```

The following properties are required:

- `:id`
- `:title`

When a property can be localised its value can have two forms e.g. `"foo"` (non-localised form) or `[:en "foo"]` (localised form).
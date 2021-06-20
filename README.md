⚠️ Until further notice `fahrenheit` remains an experiment in language design and is under active development. I would be extremely thankful for any feedback!


# fahrenheit-lang

`fahrenheit` is a domain-specific language for authoring citations and bibliographies styles. It compiles to [CSL][].



## Why?

[CSL][] has become the _de facto_ standard for codifying citations and bibliographies styles. It is widely adopted and has excellent documentation. I have no intention to change that.

However authoring citations and bibliographies styles in XML can be tedious and/or verbose. The `fahrenheit` language aims to offer an improved styles authoring experience whilst complying to the [CSL][] specification.

Here's a contrived example:

```clojure
{:about
  {:id "http://www.zotero.org/styles/apa"
   :title [:en "American Psychological Association 7th edition"]
   :title-short "APA"}}
```

The above code compiles to:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<style version="1.0">
  <info>
    <id>http://www.zotero.org/styles/apa</id>
    <title xml:lang="en">American Psychological Association 7th edition</title>
    <title-short>APA</title-short>
  </info>
</style>
```

[CSL]: https://citationstyles.org/

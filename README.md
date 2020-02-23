⚠️ Until further notice `fahrenheit` remains an experiment in language design and is under active development. I would be extremely thankful for any feedback!


# fahrenheit-lang

`fahrenheit` is a domain-specific language for authoring citations and bibliographies styles. It compiles to [CSL][csl-homepage].



## Why?

[CSL][csl-homepage] has become the _de facto_ standard for codifying citations and bibliographies styles. It is widely adopted and has excellent documentation. I have no intention to change that.

However authoring citations and bibliographies styles in XML can be tedious and/or verbose.

The `fahrenheit` language aims to offer an improved styles authoring experience while complying to the [CSL][csl-homepage] specification.

Here's a contrived example:

```
about {
  title         "My Style"
  id            "https://example.com/styles/my-style"
  url           "https://example.com/styles/my-style"
  documentation "https://example.com/styles/my-style/doc"

  author "Rick Deckard"
  author "Roy Batty"

  author "Rachael" {
    email "rachael@example.com"
    website "https://example.com/~rachael"
  }
}
```

The above code compiles to:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<style>
  <info>
    <title>My Style</title>
    <id>https://example.com/styles/my-style</id>
    <link href="https://example.com/styles/my-style" rel="self"/>
    <link href="https://example.com/styles/my-style/doc" rel="documentation"/>
    <author>
      <name>Rick Deckard</name>
    </author>
    <author>
      <name>Roy Batty</name>
    </author>
    <author>
      <name>Rachael</name>
      <email>rachael@example.com</email>
      <uri>https://example.com/~rachael</uri>
    </author>
  </info>
</style>
````

## FAQ

### Why did you call your language "Fahrenheit"?

This is a reference to [Farhenheit 451][the-book] a novel by Ray Bradbury that depicts a future where books are outlawed and burned.


[csl-homepage]: https://citationstyles.org/
[the-book]: https://en.wikipedia.org/wiki/Fahrenheit_451

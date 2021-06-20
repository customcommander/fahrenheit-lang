<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:csl="http://purl.org/net/xbiblio/csl"
  xmlns:str="http://exslt.org/strings"
  xmlns:exsl="http://exslt.org/common"
  extension-element-prefixes="str exsl">

<xsl:output method="text"/>

<xsl:template match="csl:style">
  <xsl:text>
{:about
</xsl:text>
    <xsl:apply-templates select="csl:info/csl:id"/>
    <xsl:apply-templates select="csl:info/csl:title"/>
    <xsl:apply-templates select="csl:info/csl:title-short">
      <xsl:with-param name="no-lf" select="1"/>
    </xsl:apply-templates>
<xsl:text>}
</xsl:text>
</xsl:template>

<xsl:template match="csl:*">
  <xsl:param name="no-lf"/>
  <xsl:call-template name="kv">
    <xsl:with-param name="k" select="name()"/>
    <xsl:with-param name="v" select="."/>
  </xsl:call-template>
  <xsl:if test="not($no-lf = 1)">
    <xsl:text>
</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template name="kv">
  <xsl:param name="k"/>
  <xsl:param name="v"/>
  <xsl:text>    :</xsl:text>
  <xsl:value-of select="$k"/>
  <xsl:text> </xsl:text>
  <xsl:text>"</xsl:text>
  <xsl:value-of select="$v"/>
  <xsl:text>"</xsl:text>
</xsl:template>

</xsl:stylesheet>
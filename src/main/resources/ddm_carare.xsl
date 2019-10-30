<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns="http://www.carare.eu/carareSchema"
        xmlns:dai="info:eu-repo/dai"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        exclude-result-prefixes="xs dai dc dcterms"
        version="2.0">

    <xsl:template match="/">
        <xsl:call-template name="metadata-root"/>
    </xsl:template>


    <!-- ==================================================== -->
    <xsl:template name="metadata-root">
<!--        <xsl:attribute name="xsi:schemaLocation" select="'http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema'"/>-->
        <xsl:apply-templates select="bagmetadata"/>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:template match="bagmetadata">
        <xsl:element name="digitalResource">
            <xsl:apply-templates select="ddm:DDM"/>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:template match="ddm:DDM">
        <xsl:element name="metadata">
            <xsl:apply-templates select="ddm:dcmiMetadata"/>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata">
        <xsl:apply-templates select="dcterms:rightsHolder"/>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!-- dcterms:rightsHolder to .... -->
    <!-- ==================================================== -->
    <xsl:template match="dcterms:rightsHolder">
        <xsl:element name="rightsHolder">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>


</xsl:stylesheet>

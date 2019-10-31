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

        <xsl:attribute name="xsi:schemaLocation" select="'http://www.w3.org/2001/XMLSchema http://www.w3.org/2001/XMLSchema'"/>

        <xsl:apply-templates select="bagmetadata"/>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:template match="bagmetadata">
        <xsl:element name="carare">
            <xsl:attribute name="id">
                <xsl:value-of select="'tunnus'"/>
            </xsl:attribute>
            <xsl:apply-templates select="ddm:DDM"/>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:template match="ddm:DDM">
        <xsl:call-template name="collectionInformation"/>
        <xsl:call-template name="digitalResource"/>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!-- Carare collectionInformation -->
    <!-- ==================================================== -->
    <xsl:template name="collectionInformation">
        <xsl:element name="collectionInformation">
            <title>
                <xsl:text>Whatshouldiwritehere?</xsl:text>
            </title>
            <contacts>
                <name>Drs. Hella Hollander</name>
                <role lang="en">data archivist archaeology</role>
                <organization>Data Archiving and Networked Services (DANS)</organization>
                <address>
                    <numberInRoad>51</numberInRoad>
                    <roadName>Anna van Saksenlaan</roadName>
                    <townOrCity>The Hague</townOrCity>
                    <postcodeOrZipcode>2593 HW</postcodeOrZipcode>
                    <country>the Netherlands</country>
                </address>
                <phone>+31 70 3494450</phone>
                <email>hella.hollander@dans.knaw.nl</email>
                <email>info@dans.knaw.nl</email>
            </contacts>
            <keywords lang="en">
                <xsl:text>data archive; datasets; publications; archaeological research; Archaeology; the Netherlands</xsl:text>
            </keywords>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!-- ... -->
    <!-- ==================================================== -->
    <xsl:template name="digitalResource">
        <xsl:element name="digitalResource">
            <recordInformation>
                <id>
                    <xsl:value-of select="'tunnus-2'"/>
                </id>
                <source>DANS-KNAW</source>
                <country>NLD</country>
                <creation>
                    <date>
                        <xsl:value-of select="'2015'"/>
<!--                        <xsl:value-of select="ddm:profile/ddm:created"/>-->
                    </date>
                    <actor>
                        <name lang="en">dataset depositor (locally known)</name>
                        <actorType lang="en">individual</actorType>
                        <roles lang="en">depositor</roles>
                    </actor>
                </creation>
<!--                <language>-->
<!--                    <xsl:value-of select="$meta_taal"/>-->
<!--                </language>-->
                <rights>
                    <accessRights>
                        <grantedTo lang="en">
                            <xsl:text>everyone</xsl:text>
                        </grantedTo>
                        <statement lang="en">
                            <xsl:text>metadata of the archived dataset is freely available to everyone (open access)</xsl:text>
                        </statement>
                    </accessRights>
                </rights>
            </recordInformation>

        </xsl:element>
    </xsl:template>
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

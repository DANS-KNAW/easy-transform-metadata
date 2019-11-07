<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns="http://www.carare.eu/carareSchema"
        xmlns:bagmetadata="http://easy.dans.knaw.nl/schemas/bag/metadata/bagmetadata/"
        xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:files="http://easy.dans.knaw.nl/schemas/bag/metadata/files/"
        xmlns:dcx-dai="http://easy.dans.knaw.nl/schemas/dcx/dai/"
        xmlns:gml="http://www.opengis.net/gml"
        xmlns:dcx-gml="http://easy.dans.knaw.nl/schemas/dcx/gml/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:str="http://exslt.org/strings"
        exclude-result-prefixes="xs xsi dai dc dcterms dcx-dai gml dcx-gml str bagmetadata ddm files"
        version="2.0">

    <xsl:template match="/">
        <xsl:call-template name="metadata-root"/>
    </xsl:template>


    <!-- ==================================================== -->
    <xsl:template name="metadata-root">
        <xsl:apply-templates select="bagmetadata:bagmetadata"/>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!--                    Carare wrap                       -->
    <!-- ==================================================== -->
    <xsl:template match="bagmetadata:bagmetadata">
        <xsl:element name="carareWrap">
            <xsl:element name="carare">
                <xsl:attribute name="id">
                    <xsl:value-of select="'tunnus'"/>
                </xsl:attribute>
                <xsl:apply-templates select="ddm:DDM"/>
                <xsl:apply-templates select="files:files/files:file"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!--            Carare collectionInformation              -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:DDM">
        <xsl:element name="collectionInformation">

            <!-- title -->
            <xsl:apply-templates select="ddm:profile/dc:title| ddm:profile/dcterms:title | ddm:profile/dcterms:alternative"/>

            <!-- contacts -->
            <xsl:call-template name="contacts"/>

            <!-- rights -->
            <xsl:call-template name="rights"/>

            <!-- language -->
            <xsl:apply-templates select="ddm:dcmiMetadata/dcterms:language | ddm:dcmiMetadata/dc:language"/>

            <!-- statement -->
            <xsl:call-template name="statement"/>

            <!-- creation -->
            <xsl:call-template name="creation"/>

            <!-- coverage -->
            <xsl:call-template name="coverage"/>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      title                           -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:profile/dc:title | ddm:profile/dcterms:title | ddm:profile/dcterms:alternative">
        <xsl:element name="title">
            <xsl:if test="./@xml:lang">
                <xsl:attribute name="lang">
                    <xsl:value-of select="./@xml:lang" />
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                        contacts                        -->
    <!-- ==================================================== -->
    <xsl:template name="contacts">
        <xsl:element name="contacts">
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
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                        rights                        -->
    <!-- ==================================================== -->
    <xsl:template name="rights">
        <xsl:element name="rights">
            <xsl:if test="ddm:dcmiMetadata/dcterms:rightsHolder">
                <copyrightCreditLine>
                    <xsl:value-of select="ddm:dcmiMetadata/dcterms:rightsHolder"/>
                </copyrightCreditLine>
            </xsl:if>
            <accessRights>
                <xsl:value-of select="ddm:profile/ddm:accessRights"/>
            </accessRights>
            <xsl:if test="ddm:dcmiMetadata/dcterms:license[@xsi:type=&apos;dcterms:URI&apos;]">
                <licence>
                    <xsl:value-of select="ddm:dcmiMetadata/dcterms:license[@xsi:type=&apos;dcterms:URI&apos;]"/>
                </licence>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                       language                       -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/dcterms:language | ddm:dcmiMetadata/dc:language">
        <xsl:element name="language">
            <xsl:if test="./@xsi:type">
                <xsl:attribute name="lang">
                    <xsl:value-of select="./@xsi:type" />
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      statement                       -->
    <!-- ==================================================== -->
    <xsl:template name="statement">
        <xsl:for-each select="ddm:profile/dc:description | ddm:profile/dcterms:description | ddm:profile/dcterms:abstract| ddm:profile/dcterms:tableOfContents">
            <statement><xsl:value-of select="."/></statement>
        </xsl:for-each>
        <xsl:for-each select="ddm:profile/dc:creator | ddm:profile/dcterms:creator | ddm:profile/dcx-dai:creator | ddm:profile/dcx-dai:creatorDetails">
            <statement>Creator: <xsl:value-of select="."/></statement>
        </xsl:for-each>
        <xsl:for-each select="ddm:profile/ddm:audience">
            <statement>Audience: <xsl:value-of select="."/></statement>
        </xsl:for-each>
        <xsl:for-each select="ddm:profile/ddm:available">
            <statement>Available: <xsl:value-of select="."/></statement>
        </xsl:for-each>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      creation                        -->
    <!-- ==================================================== -->
    <xsl:template name="creation">
        <xsl:element name="creation">
            <xsl:element name="createdOn">
                <xsl:value-of select="ddm:profile/ddm:created"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                        coverage                      -->
    <!-- ==================================================== -->
    <xsl:template name="coverage">
        <xsl:element name="coverage">
            <!-- coverage / temporal -->
            <xsl:for-each select="ddm:dcmiMetadata/dc:coverage[@xsi:type=&apos;dct:Period&apos;]" >
                <temporal>
                    <displayDate><xsl:value-of select="."/></displayDate>
                </temporal>
            </xsl:for-each>
            <xsl:for-each select="ddm:dcmiMetadata/dcterms:temporal[not(@xsi:type=&apos;abr:ABRperiode&apos;)]" >
                <temporal>
                    <displayDate><xsl:value-of select="."/></displayDate>
                </temporal>
            </xsl:for-each>
            <xsl:for-each select="ddm:dcmiMetadata/dcterms:temporal[@xsi:type=&apos;abr:ABRperiode&apos;]" >
                <temporal>
                    <periodName><xsl:value-of select="."/></periodName>
                </temporal>
            </xsl:for-each>

            <!-- coverage / spatial -->
            <xsl:for-each select="ddm:dcmiMetadata/dcterms:spatial[not(@xsi:type)]" >
                <spatial>
                    <locationSet>
                        <namedLocation><xsl:value-of select="."/></namedLocation>
                    </locationSet>
                </spatial>
            </xsl:for-each>
            <xsl:apply-templates select="ddm:dcmiMetadata/dcterms:spatial[@xsi:type=&apos;dcx-gml:SimpleGMLType&apos;]/gml:Point/gml:pos
                                           | ddm:dcmiMetadata/dcx-gml:spatial/gml:Point/gml:pos"/>
        </xsl:element>
    </xsl:template>


    <!-- ==================================================== -->
    <!--      coverage / spatial / geometry / quickpoint      -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/dcterms:spatial[@xsi:type=&apos;dcx-gml:SimpleGMLType&apos;]/gml:Point/gml:pos
                       | ddm:dcmiMetadata/dcx-gml:spatial/gml:Point/gml:pos">
        <xsl:variable name="coordinates" select="normalize-space(.)"/>
        <xsl:variable name="pos" select="str:tokenize($coordinates, ' ')"/>
        <xsl:element name="spatial">
            <geometry>
                <quickpoint>
                    <xsl:if test="$pos[1]">
                        <x><xsl:value-of select="$pos[1]"/></x>
                    </xsl:if>
                    <xsl:if test="$pos[2]">
                        <y><xsl:value-of select="$pos[2]"/></y>
                    </xsl:if>
                    <xsl:if test="$pos[3]">
                        <z><xsl:value-of select="$pos[3]"/></z>
                    </xsl:if>
                </quickpoint>
            </geometry>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--               Carare digitalResource                 -->
    <!-- ==================================================== -->
    <xsl:template match="files:files/files:file">
        <xsl:element name="digitalResource">

            <!-- recordInformation -->
            <recordInformation>
                <id><xsl:value-of select="./@filepath"/></id>
            </recordInformation>

            <!-- appellation -->
            <xsl:variable name="pathParts" select="str:tokenize(./@filepath, '/')"/>
            <appellation>
                <name lang="en"><xsl:value-of select="$pathParts[last()]"/></name>
                <id><xsl:value-of select="./@filepath"/></id>
            </appellation>

            <!-- description -->
            <xsl:for-each select="dcterms:description">
                <description lang="en">
                    <xsl:value-of select="."/>
                </description>
            </xsl:for-each>

            <!-- format -->
            <format>
                <xsl:value-of select="dcterms:format"/>
            </format>

            <!-- link -->
            <link>
                <xsl:value-of select="dcterms:source"/>
            </link>

            <!-- object -->
            <object>
                <xsl:value-of select="dcterms:source"/>
            </object>

            <!-- rights -->
            <rights>
                <accessRights>
                    <xsl:value-of select="files:accessibleToRights"/>
                </accessRights>
            </rights>

        </xsl:element>
    </xsl:template>

</xsl:stylesheet>

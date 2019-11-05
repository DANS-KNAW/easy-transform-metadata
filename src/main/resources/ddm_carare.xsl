<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns="http://www.carare.eu/carareSchema"
        xmlns:bagmetadata="http://easy.dans.knaw.nl/schemas/bag/metadata/bagmetadata/"
        xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/"
        xmlns:dai="info:eu-repo/dai"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:dcx-dai="http://easy.dans.knaw.nl/schemas/dcx/dai/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        exclude-result-prefixes="xs xsi dai dc dcterms dcx-dai bagmetadata ddm"
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
    <xsl:template match="bagmetadata:bagmetadata">
        <xsl:element name="carareWrap">
            <xsl:element name="carare">
                <xsl:attribute name="id">
                    <xsl:value-of select="'tunnus'"/>
                </xsl:attribute>
                <xsl:apply-templates select="ddm:DDM"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <xsl:template match="ddm:DDM">
        <xsl:call-template name="collectionInformation"/>
<!--        <xsl:call-template name="digitalResource"/>-->
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!-- Carare collectionInformation -->
    <!-- ==================================================== -->
    <xsl:template name="collectionInformation">
        <xsl:element name="collectionInformation">

            <!-- title -->
            <xsl:for-each select="ddm:profile/dc:title[@xml:lang]" >
                <xsl:variable name="lang" select="./@xml:lang"/>
                <title lang="{$lang}">
                    <xsl:value-of select="."/>
                </title>
            </xsl:for-each>
            <xsl:for-each select="ddm:profile/dc:title[not(@xml:lang)]" >
                <title>
                    <xsl:value-of select="."/>
                </title>
            </xsl:for-each>

            <!-- contacts -->
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

            <!-- rights -->
            <rights>
                <xsl:if test="ddm:dcmiMetadata/dcterms:rightsHolder">
                    <copyrightCreditLine>
                        <xsl:value-of select="ddm:dcmiMetadata/dcterms:rightsHolder"/>
                    </copyrightCreditLine>
                </xsl:if>
                <accessRights>
                    <xsl:value-of select="ddm:profile/ddm:accessRights"/>
                </accessRights>
                <xsl:if test="ddm:dcmiMetadata/dcterms:license[@xsi:type=&apos;dcterms:URI&apos;]">
                    <license>
                        <xsl:value-of select="ddm:dcmiMetadata/dcterms:license[@xsi:type=&apos;dcterms:URI&apos;]"/>
                    </license>
                </xsl:if>
            </rights>

            <!-- language -->
            <xsl:for-each select="ddm:dcmiMetadata/dcterms:language[@xsi:type]" >
                <xsl:variable name="type" select="./@xml:lang"/>
                <language lang="{$type}">
                    <xsl:value-of select="."/>
                </language>
            </xsl:for-each>
            <xsl:for-each select="ddm:dcmiMetadata/dcterms:language[not(@xsi:type)]" >
                <language>
                    <xsl:value-of select="."/>
                </language>
            </xsl:for-each>
            <xsl:for-each select="ddm:dcmiMetadata/dc:language[@xsi:type]" >
                <xsl:variable name="type" select="./@xml:lang"/>
                <language lang="{$type}">
                    <xsl:value-of select="."/>
                </language>
            </xsl:for-each>
            <xsl:for-each select="ddm:dcmiMetadata/dc:language[not(@xsi:type)]" >
                <language>
                    <xsl:value-of select="."/>
                </language>
            </xsl:for-each>

            <!-- statements -->
            <xsl:for-each select="ddm:profile/dc:description">
                <statement><xsl:value-of select="."/></statement>
            </xsl:for-each>
            <xsl:for-each select="ddm:profile/dc:creator">
                <statement>Creator: <xsl:value-of select="."/></statement>
            </xsl:for-each>
            <xsl:for-each select="ddm:profile/dcx-dai:creatorDetails">
                <statement>Creator: <xsl:value-of select="."/></statement>
            </xsl:for-each>
            <xsl:for-each select="ddm:profile/ddm:audience">
                <statement>Audience: <xsl:value-of select="."/></statement>
            </xsl:for-each>
            <xsl:for-each select="ddm:profile/ddm:available">
                <statement>Available: <xsl:value-of select="."/></statement>
            </xsl:for-each>

            <!-- creation -->
            <creation>
                <createdOn>
                    <xsl:value-of select="ddm:profile/ddm:created"/>
                </createdOn>
            </creation>


            <!-- coverage -->
            <coverage>
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
                <xsl:if test="ddm:dcmiMetadata/dcterms:spatial[not(@xsi:type)]">
                    <spatial>
                        <locationSet>
                            <xsl:for-each select="ddm:dcmiMetadata/dcterms:spatial[not(@xsi:type)]" >
                                <namedLocation><xsl:value-of select="."/></namedLocation>
                            </xsl:for-each>
                        </locationSet>
                    </spatial>
                </xsl:if>
            </coverage>


        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!-- Carare digitalResource --> -->
    <!-- ==================================================== -->
    <xsl:template name="digitalResource">
        <xsl:element name="digitalResource">


            <!-- recordInformation -->
            <recordInformation>
                <id>
                    <xsl:value-of select="'tunnus-2'"/>
                </id>
                <source>DANS-KNAW</source>
                <country>NLD</country>
<!--                <creation>-->
<!--                    <xsl:if test="translate(ddm:profile/ddm:created, '123456789', '000000000') = '0000-00-00'">-->
<!--                        <date>-->
<!--                            <xsl:value-of select="ddm:profile/ddm:created"/>-->
<!--                        </date>-->
<!--                    </xsl:if>-->
<!--                </creation>-->
                <metadataRights>
                </metadataRights>
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

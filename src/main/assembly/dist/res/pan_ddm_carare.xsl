<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns="http://www.carare.eu/carareSchema"
        xmlns:bagmetadata="http://easy.dans.knaw.nl/schemas/bag/metadata/bagmetadata/"
        xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/"
        xmlns:files="http://easy.dans.knaw.nl/schemas/bag/metadata/files/"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        xmlns:dcx-dai="http://easy.dans.knaw.nl/schemas/dcx/dai/"
        xmlns:gml="http://www.opengis.net/gml"
        xmlns:dcx-gml="http://easy.dans.knaw.nl/schemas/dcx/gml/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:str="http://exslt.org/strings"
        exclude-result-prefixes="xs xsi dc dcterms dcx-dai gml dcx-gml str bagmetadata ddm files">

    <xsl:variable name="doi" select="bagmetadata:bagmetadata/ddm:DDM/ddm:dcmiMetadata/dcterms:identifier[@xsi:type=&apos;id-type:DOI&apos;]"/>
    <xsl:variable name="doi-url" select="concat('https://doi.org/', $doi)"/>

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
                    <xsl:value-of select="$doi"/>
                </xsl:attribute>

                <!-- collectionInformation -->
                <xsl:call-template name="collectionInformation"/>

                <!-- heritageAssetIdentification -->
                <xsl:apply-templates select="ddm:DDM"/>

                <!-- digitalResource -->
                <xsl:apply-templates select="files:files/files:file"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!-- ==================================================== -->

    <!-- ==================================================== -->
    <!--            Carare collectionInformation              -->
    <!-- ==================================================== -->
    <xsl:template name="collectionInformation">
        <xsl:element name="collectionInformation">

            <title lang="en">
                <xsl:text>e-archive Dutch Archaeology (DANS-EDNA)</xsl:text>
            </title>
            <title lang="nl" preferred="true">
                <xsl:text>e-depot Nederlandse archeologie (DANS-EDNA)</xsl:text>
            </title>
            <source>DANS-KNAW</source>
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
            <rights>
                <reproductionRights lang="en">
                        <xsl:text>Allowed for research and educational use only. For personal reuse only, reproduction or redistribution in any form is not allowed, no commercial use allowed. Attribution compulsory</xsl:text>
                 </reproductionRights>
                <licence>http://www.dans.knaw.nl/en/content/data-archive/terms-and-conditions</licence>
            </rights>
            <language>nl</language>
            <keywords lang="en">
                <xsl:text>data archive; datasets; publications; archaeological research; Archaeology; the Netherlands</xsl:text>
            </keywords>
            <coverage>
                <spatial>
                    <locationSet>
                        <geopoliticalArea lang="en">the Netherlands</geopoliticalArea>
                    </locationSet>
                </spatial>
            </coverage>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--          Carare heritageAssetIdentification          -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:DDM">
        <xsl:element name="heritageAssetIdentification">

            <!-- recordInformation -->
            <xsl:call-template name="heritageRecordInformation"/>

            <!-- appellation -->
            <xsl:call-template name="appellation"/>

            <!-- description -->
            <xsl:apply-templates select="ddm:profile/dcterms:description"/>

            <!-- actors -->
            <xsl:apply-templates select="ddm:profile/dcx-dai:creatorDetails"/>

            <!-- characters -->
            <xsl:call-template name="characters"/>

            <!-- spatial -->
            <xsl:apply-templates select="ddm:dcmiMetadata/dcterms:spatial | ddm:dcmiMetadata/dcx-gml:spatial//gml:name"/>

            <!-- publicationStatement -->
            <xsl:apply-templates select="ddm:dcmiMetadata/dcterms:publisher"/>

            <!-- rights -->
            <xsl:call-template name="rights"/>

            <!-- references -->
            <xsl:apply-templates select="ddm:dcmiMetadata/ddm:references"/>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--   heritageAssetIdentification / recordInformation    -->
    <!-- ==================================================== -->
    <xsl:template name="heritageRecordInformation">
        <xsl:element name="recordInformation">

            <!-- id -->
            <id>
                <xsl:value-of select="$doi"/>
            </id>

            <!-- creation -->
            <creation>
                <date>
                    <xsl:value-of select="substring(ddm:profile/ddm:created, 0, 11)"/>
                </date>
            </creation>

            <!-- language -->
            <xsl:apply-templates select="ddm:dcmiMetadata/dc:language"/>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                       language                       -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/dc:language">
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
    <!--                     appellation                      -->
    <!-- ==================================================== -->
    <xsl:template name="appellation">
        <xsl:variable name="title" select="ddm:profile/dc:title"/>
        <xsl:element name="appellation">
            <xsl:element name="name">
                <xsl:attribute name="lang">
                    <xsl:if test="$title/@xml:lang">
                        <xsl:value-of select="$title/@xml:lang" />
                    </xsl:if>
                    <xsl:if test="not($title/@xml:lang)">
                        <xsl:value-of select="'en'" />
                    </xsl:if>
                </xsl:attribute>
                <xsl:value-of select="$title"/>
            </xsl:element>
            <xsl:element name="id">
                <xsl:value-of select="$doi"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     description                      -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:profile/dcterms:description">
        <xsl:element name="description">
            <xsl:if test="./@xml:lang">
                <xsl:attribute name="lang">
                    <xsl:value-of select="./@xml:lang" />
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      actors                          -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:profile/dcx-dai:creatorDetails">
        <xsl:element name="actors">

            <!-- name -->
            <xsl:for-each select=".//dcx-dai:name">
                <xsl:element name="name">
                    <xsl:if test="./@xml:lang">
                        <xsl:attribute name="lang">
                            <xsl:value-of select="./@xml:lang" />
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <!-- actorType -->
            <xsl:if test="dcx-dai:organization">
                <xsl:element name="actorType">
                    <xsl:if test="./@xml:lang">
                        <xsl:attribute name="lang">
                            <xsl:value-of select="./@xml:lang" />
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="'organization'"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="dcx-dai:author">
                <xsl:element name="actorType">
                    <xsl:if test="./@xml:lang">
                        <xsl:attribute name="lang">
                            <xsl:value-of select="./@xml:lang" />
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="'individual'"/>
                </xsl:element>
            </xsl:if>

            <!-- roles -->
            <xsl:for-each select=".//dcx-dai:role">
                <xsl:element name="roles">
                    <xsl:if test="./@xml:lang">
                        <xsl:attribute name="lang">
                            <xsl:value-of select="./@xml:lang" />
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     characters                       -->
    <!-- ==================================================== -->
    <xsl:template name="characters">
        <xsl:element name="characters">

            <!-- heritageAssetType -->
            <xsl:apply-templates select="ddm:dcmiMetadata/ddm:subject"/>

            <!-- temporal -->
            <xsl:apply-templates select="ddm:dcmiMetadata/ddm:temporal[@schemeURI=&apos;https://data.cultureelerfgoed.nl/term/id/abr/b6df7840-67bf-48bd-aa56-7ee39435d2ed&apos;]"/>

            <!-- materials -->
            <xsl:apply-templates select="ddm:dcmiMetadata/dc:subject"/>

        </xsl:element>
    </xsl:template>


    <!-- ==================================================== -->
    <!--                 heritageAssetType                    -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/ddm:subject">
        <xsl:element name="heritageAssetType">
            <xsl:if test="./@xml:lang">
                <xsl:attribute name="lang">
                    <xsl:value-of select="./@xml:lang" />
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     temporal                         -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/ddm:temporal[@schemeURI=&apos;https://data.cultureelerfgoed.nl/term/id/abr/b6df7840-67bf-48bd-aa56-7ee39435d2ed&apos;]">
        <xsl:element name="temporal">
            <xsl:element name="displayDate">
                <xsl:if test="./@xml:lang">
                    <xsl:attribute name="lang">
                        <xsl:value-of select="./@xml:lang" />
                    </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                    materials                         -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/dc:subject">
        <xsl:element name="materials">
            <xsl:if test="./@xml:lang">
                <xsl:attribute name="lang">
                    <xsl:value-of select="./@xml:lang" />
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      spatial                         -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/dcterms:spatial | ddm:dcmiMetadata/dcx-gml:spatial//gml:name">
        <xsl:element name="spatial">
            <xsl:element name="locationSet">
                <xsl:element name="namedLocation">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>


    <!-- ==================================================== -->
    <!--                 publicationStatement                 -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/dcterms:publisher">
        <xsl:element name="publicationStatement">
            <xsl:element name="publisher">
                <xsl:value-of select="."/>
            </xsl:element>
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

            <xsl:variable name="license" select="ddm:dcmiMetadata/dcterms:license[@xsi:type=&apos;dcterms:URI&apos;]"/>
            <xsl:if test="$license">

                <licence>
                    <xsl:value-of select="$license"/>
                </licence>

                <europeanaRights>
                    <xsl:choose>
                        <xsl:when test="contains($license, 'publicdomain/zero/1.0')">
                            <xsl:value-of select="'The Creative Commons CC0 1.0 Universal Public Domain Dedication (CC0)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by/4.0')">
                            <xsl:value-of select="'Creative Commons - Attribution (BY)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-sa/4.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, ShareAlike (BY-SA)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-nc/4.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial (BY-NC)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-nc/3.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial (BY-NC)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-nd/4.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, No Derivatives (BY-ND)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-nc-nd/4.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial, No Derivatives (BY-NC-ND)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-nc-sa/4.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial, ShareAlike (BY-NC-SA)'"/>
                        </xsl:when>
                        <xsl:when test="contains($license, 'licenses/by-nc-sa/3.0')">
                            <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial, ShareAlike (BY-NC-SA)'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="'Copyright Not Evaluated (CNE)'"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </europeanaRights>
            </xsl:if>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      references                      -->
    <!-- ==================================================== -->
    <xsl:template match="ddm:dcmiMetadata/ddm:references">
        <xsl:variable name="title" select="ddm:profile/dc:title"/>
        <xsl:element name="references">
            <xsl:element name="appellation">
                <xsl:element name="name">
                    <xsl:attribute name="lang">
                        <xsl:if test="./@xml:lang">
                            <xsl:value-of select="./@xml:lang" />
                        </xsl:if>
                        <xsl:if test="not(./@xml:lang)">
                            <xsl:value-of select="'en'" />
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
                <xsl:element name="id">
                    <xsl:value-of select="./@href"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>


    <!-- ==================================================== -->
    <!--               Carare digitalResource                 -->
    <!-- ==================================================== -->
    <xsl:template match="files:files/files:file">

        <xsl:if test="files:accessibleToRights='ANONYMOUS' and files:visibleToRights='ANONYMOUS'">

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

                <!-- isShownAt -->
                <isShownAt>
                    <xsl:value-of select="$doi-url"/>
                </isShownAt>

                <!-- rights -->
                <rights>
                    <accessRights>
                        <xsl:value-of select="files:accessibleToRights"/>
                    </accessRights>
                </rights>

            </xsl:element>

        </xsl:if>
    </xsl:template>

</xsl:stylesheet>

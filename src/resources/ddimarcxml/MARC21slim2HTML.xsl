<?xml version="1.0" encoding="UTF-8"?>
<!-- Base on the stylesheets from: http://www.loc.gov/standards/marcxml/ -->
<xsl:stylesheet version="1.0" xmlns:marc="http://www.loc.gov/MARC21/slim" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	
	<xsl:template match="/">
		<html><head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      </head>
			<xsl:apply-templates/>
		</html>
	</xsl:template>
	
	<xsl:template match="marc:record">
		<table>
			<tr>
				<th NOWRAP="TRUE" ALIGN="RIGHT" VALIGN="TOP">
					000
				</th>
				<td>
					<xsl:value-of select="marc:leader"/>
				</td>
			</tr>
			<xsl:apply-templates select="marc:datafield|marc:controlfield"/>
		</table>
	</xsl:template>
	
	<xsl:template match="marc:controlfield">
		<tr>
			<th NOWRAP="TRUE" ALIGN="RIGHT" VALIGN="TOP">
				<xsl:value-of select="@tag"/>
			</th>
			<td>
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="marc:datafield">
		<tr>
			<th NOWRAP="TRUE" ALIGN="RIGHT" VALIGN="TOP">
				<xsl:value-of select="@tag"/>
			</th>
			<td>
				<xsl:value-of select="@ind1"/>
				<xsl:value-of select="@ind2"/>
				<xsl:apply-templates select="marc:subfield"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="marc:subfield">
		<strong>|<xsl:value-of select="@code"/></strong> <xsl:value-of select="."/>
	</xsl:template>

</xsl:stylesheet>
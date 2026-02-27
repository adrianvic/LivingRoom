<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/static/styles.css">
    <title><#if pageTitle??>${pageTitle}<#else>LivingRoom</#if></title>
</head>
<body <#if bodyID??>id="${bodyID}"</#if><#if accentColor??> style="--accent-color: ${accentColor};"</#if>>
<#include "header.ftl">
<main>
    <#if pageContent??>${pageContent}</#if>
</main>
</body>
</html>
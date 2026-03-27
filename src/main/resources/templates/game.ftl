<#assign pageTitle = game["name"]>
<#assign bodyID = "gamePage">
<#assign pageContent>
<div class="game">
    <div class="gameImageContainer">
        <img class="gameImage" src="/pic/${game["id"]}">
        <img class="gameDisk" src="/static/images/dvd.png">
        <div class="gameDownloads">
            <#if versions?has_content>
            <div class="downloads">
                <h3>Available Downloads</h3>
                <ul>
                    <#list versions as version>
                    <li><a href="/download/${game["id"]}/${version}">${version}</a></li>
                    </#list>
                </ul>
            </div>
            </#if>
        </div>
    </div>
    <div class="gameInfo">
        <div class="gameTitle">
            <h2>${game["name"]}<#if game["author"]?has_content><span class="gameAuthorText"> by ${game["author"]}</span></#if></h2>
            <#if game["year"]?has_content>
            <p>${game["year"]}</p>
            </#if>
        </div>
        
        <div class="downloadsButton">
            <button onclick="document.querySelector('.gameImageContainer').classList.toggle('showDownloads');">Downloads</button>
        </div>
        
        <div class="gameProperties">
            <#if game["publisher"]?has_content>
            <p><b>Published by</b> ${game["publisher"]}</p>
            </#if>
            <#if game["date"]?has_content>
            <p><b>Publishing date:</b> ${game["date"]}</p>
            </#if>
            <#if game["operating_system"]?has_content>
            <p><b>Made for</b> ${game["operating_system"]}</p>
            </#if>
            <#if game["description"]?has_content>
            <p><b>Description: </b></p>
            <div class="gameDescription">
                <p>${game["description"]}</p>
            </div>
            </#if>
            <#if userRole == "admin"><p>Admin actions: <a href="/${webpref}/remove/${game["id"]}">deindex</a>, <a href="/${webpref}/scan">trigger new scan</a></p></#if>
        </div>
    </div>
</div>
</#assign>
<#include "base.ftl">
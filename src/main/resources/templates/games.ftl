<#assign pageTitle = "Games">
<#assign pageContent>
<h2>Games</h2>
<div id="games">
<#list games as game>
    <div class="gameCard" style="--accent-color: ${game["accentColor"]};">
        <a href="/${webpref}/game/${game["ID"]}">
        <img class="gameImage" src="/pic/${game["ID"]}">
        <p>${game["NAME"]}</p>
        </a>
    </div>
</#list>
</div>
</#assign>
<#include "base.ftl">
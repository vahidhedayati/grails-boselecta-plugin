Bo selecta! 
=========

Grails plugin that uses default WebSocket technology to interact with your domainClasses and produce dependent form / options that depend on one another. The format to define select functionality / auto complete are identical. Auto complete requires additional boolean values to be passed to make it auto complete.


##### Select box dependencies data comes from WebSocket Client running within your application (as part of the plugin)
##### AutoComplete input areas use HTML5 dataList, data for dataList provided by backend WebSocket Connection.
 

 ### [Video Everything you need to know ](https://www.youtube.com/watch?v=CFPe9pSFJ3g)
  
![diagram](https://raw.githubusercontent.com/vahidhedayati/grails-boselecta-plugin/grails2/documentation/boselecta.png)


 BoSelecta can be incorporated to an existing grails app running ver 2>+. Supports both resource (pre 2.4) /assets (2.4+) based grails sites.

###### Plugin will work with tomcat 7.0.54 + (inc. 8) running java 1.7+

###### Dependency

Dependency Grails 3:
```groovy
    compile "org.grails.plugins:boselecta:3.0.1"
```
[codebase for grails 3.X](https://github.com/vahidhedayati/grails-boselecta-plugin/)

     
Dependency Grails 2: (not released refer to testing Plugin below)     
```groovy
	compile ":boselecta:0.1" 
```
[codebase for grails 2.X](https://github.com/vahidhedayati/grails-boselecta-plugin/tree/grails2)

###Latest updates (after video published):
A further lock down has been introduced, with the assumption that most people will be looking up domain objects with single dependencies.
This means, typically I expect an end user to map from continent to country to city and so on. So its one to one relationship between each object. For this reason the default call now only supports one relation, so:
```gsp 
<bo:selecta
...  
domain=""
..
domain2=""
..
/>
```
If you attempt to exceed and use domain3 and on. The plugin will not work. You can from now use selecta2 for those relations with depth 
```gsp 
<bo:selecta2
domainDepth="4"
...  
domain=""
..
domain2=""
..
domain3=""
..
domain4=""
..
/>
```
 

#### [Testing plugin](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/Testing-plugin) 

#### [Config.groovy overrides](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/Config.groovy)

#### [domainDepth explained] (https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/domainDepth-explained)

##### [grails BoSelecta Plugin usage](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/grails-BoSelecta-Plugin-usage)

#### [JSON Formatting](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/JSON-Formatting).

##### [What is front-end / back-end WebSocket connections](https://github.com/vahidhedayati/grails-boselecta-plugin/wiki/What-is-front-end---back-end-WebSocket-connections) ?


##Examples:
##### Basic example:

[select 1 relation](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/index.gsp) This is all in one gsp page.

1. Initiate a master socket connection, <bo:connect handles all the information flowing back from backend websocket. So this is your main and only connection required on any given page. Take a note of the user and job names, they must match all the calls on this page.
```gsp
<bo:connect user="myuser" job="job1" />
```

Now that we have configured our master listener, lets configure one connection with a relation that is returned to a normal blank select box
```gsp
<form  action="example5">
<bo:selecta id="MyCountry1" name="MyCountry1"

job= "job1"
user="myuser"

domainDepth="0"

formatting="JSON"

domain='ajaxdependancyselectexample.MyCountry'
searchField='countryName'
collectField='id'

domain2='ajaxdependancyselectexample.MyCity'
bindid="mycountry.id"
searchField2='cityName'
collectField2='id'

appendValue=''
appendName='Updated'

noSelection="['': 'Please choose Continent']"

setId="MyCity1"
/>
<g:select name="MyCity1" id="MyCity1" optionKey="id" optionValue="cityName" from="[]" required="required" noSelection="['': 'Please choose Country']" />
<input type=submit value=go> 
</form>
```
If you have used ajaxdependancy selection, a lot of the above input will look familiar.
You are now giving the backend the ID of your primary selection MyCountry1, you are telling it what domain is which makes this a primary call and then returns MyCountry.countryName and MyCountry.id to the primary box, you are then setting the setId as MyCity1 and saying domain2 is MyCity and to search/collect cityName/id.



Multiple relationships: [select 2 relations](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/index2.gsp)

Identical to above, but in this example we iterate over the relations using <bo:select passing domain2 object which be the next object so on the secondary objects there is no domain= value defined.
```gsp
<bo:connect user="randomUser2" job="job2" />

<form  action="example5">    
    
<bo:selecta id="MyContinent2" name="MyContinent2"
    job= "job2"
    user="randomUser2"
    domainDepth="0"
    domain='ajaxdependancyselectexample.MyContinent'
    searchField='continentName'
    collectField='id'
    domain2='ajaxdependancyselectexample.MyCountry'
    bindid="mycontinent.id"
    searchField2='countryName'
    appendValue=''
    appendName='Updated'
    collectField2='id'
    noSelection="['': 'Please choose Continent']" 
    setId="MyCountry11"
    />

 <bo:selecta id="MyCountry11" name="MyCountry11"
    job= "job2"
    user="randomUser2"
    domainDepth="0"
    domain2='ajaxdependancyselectexample.MyCity'
    bindid="mycountry.id"
    searchField2='cityName'
    collectField2='id'
    
    formatting="JSON"
    appendValue=''
    appendName='Updated'
    noSelection="['': 'Please choose Continent']" 
    setId="MyCity11"
    />

    <bo:selecta name="MyCity11" id="MyCity11"  
    job= "job2"
    user="randomUser2"
    domainDepth="0"
    domain2='ajaxdependancyselectexample.MyShops'
    bindid="mycity.id"
    searchField2='shopName'
    collectField2='id'
    appendValue=''
    appendName='Updated'
    formatting="JSON"
    setId="MyShop12"
    noSelection="['': 'Please choose Country 1111']" 
    />

    <g:select name="MyShop12" id="MyShop12"  
    optionKey="id" optionValue="shopName" 
    from="[]" required="required" noSelection="['': 'Please choose City']" 
    />
    <input type=submit value=go>  
    </form>
```
    


Using the same Tag to acheive [autoComplete](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/autoComplete.gsp)
In this example (not on actual link, the user is being defined as a variable and reused - this now makes it a dynamic user but same on that one page.

The difference with this call and select is that as you can see on the first example it has autoComplete="true" and if this is the primary object then also autoCompletePrimary="true", if second object it just needs the first tag. Follow example below.. There are two additional fields hiddenField and jsonField. In autoComplete, the results are returned from html5 dataList. I have added data-value and parse that into the hidden field for jsonField and hiddenField gets set to the or collectField of the value selected. The rest is like above.
```gsp
<% def myuser = bo.randomizeUser('user': 'random1') %>
<bo:connect user="${myuser}" job="job3"/>
<form action="example5">
	
    <bo:selecta 
        autoComplete="true" 
        autoCompletePrimary="true" 
        
        job="job3" 
        user="${myuser}"
	
        id="MyContinent2" 
	name="MyContinent2" 
	hiddenField="VahidHidden_"
	jsonField="VahidJSON_"
	
	formatting="JSON"
	domain='ajaxdependancyselectexample.MyContinent'
	searchField='continentName' 
	collectField='id'
	
	domain2='ajaxdependancyselectexample.MyCountry'
	bindid="mycontinent.id" 
	searchField2='countryName' 
	collectField2='id'
	
	setId="MyCountry11" 
	/>
	
    <bo:selecta 
	autoComplete="true" 
	job="job3" 
	user="${myuser}"	
	id="MyCountry11" 
	name="MyCountry11"
	hiddenField="NextHidden_"
	jsonField="NextJSON_"
	formatting="JSON"
	domain2='ajaxdependancyselectexample.MyCity' 
	bindid="mycountry.id"
	searchField2='cityName' 
	collectField2='id' 
	setId="MyCity11" 
    />
	
    <bo:selecta
	job="job3" 
	user="${myuser}" 
	autoComplete="true"
	formatting="JSON"
	name="MyCity11" 
	id="MyCity11"
	hiddenField="myHidden_"
	jsonField="myJSON_"
	
	domain2='ajaxdependancyselectexample.MyShops' 
	bindid="mycity.id"
	searchField2='shopName' 
	collectField2='id' 
	setId="secondarySearch4"
	/>
	
    <bo:selecta 
	job= "job121"
	user="${myuser }"	
	autoComplete="true"
	formatting="JSON"
	hiddenField="myHidden_"
	jsonField="myJSON_"
	id="secondarySearch4"  
	name="NAMEOFBorough"  
	domain2='ajaxdependancyselectexample.MyBorough' 
	bindid='myborough' 
	searchField='actualName' 
	collectField='id' 
	value=''
    />
    <input type=submit value=go>
</form>
```

[autoCompleteToSelect](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/autoCompleteToSelect.gsp)

[autoCompleteToSelect2](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/autoCompleteToSelect2.gsp)

[definedselectvalues](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues.gsp)

[definedselectvalues2](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues2.gsp)

[definedselectvalues3](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/definedselectvalues3.gsp)


[multidomainexample](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/multidomainexample.gsp)

[multimultidomainexample](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/multimultidomainexample.gsp)

[no reference domain objects](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/noref.gsp)

[no reference primary object](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/norefprimary.gsp)

[noref selection to next element](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/norefselectionext.gsp)

[selectautoComplete](https://github.com/vahidhedayati/testbo/blob/master/grails-app/views/test/selectautoComplete.gsp)


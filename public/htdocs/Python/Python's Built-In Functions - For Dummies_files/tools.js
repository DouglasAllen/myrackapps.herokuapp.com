if(typeof WILEY=="undefined"){var WILEY={};}if(typeof WILEY.dummies=="undefined"){WILEY.dummies={};}if(typeof WILEY.dummies.util=="undefined"){WILEY.dummies.util={};}var multiFeatureTimer;var isIphone=false;isAndroid=false;isBlackberry=false;var agent=navigator.userAgent.toLowerCase();if(agent.indexOf("iphone")!=-1){isIphone=true;
}if(agent.indexOf("blackberry")!=-1){isBlackberry=true;}if(agent.indexOf("android")!=-1){isAndroid=true;}_initialize=function(){if($("expanding-nav")){$$("ul.submenu").each(function(D,C){D.id="submenu_"+C;});$$("ul.menu li a").each(function(C){C.style.paddingRight="35px";});$$("#topnav .expander").each(function(C){C.style.display="inline";
});_attachListeners();}$$('#wrapper a[rel="lightbox"]').each(function(D,C){Event.observe(D,"click",function(E){_showLightbox(D);Event.stop(E);});});$$('#wrapper a[rel="tooltip"]').each(function(D,C){Event.observe(D,"click",function(E){Event.stop(E);});D.id="tooltip-"+C;_createTooltip(D);});if($$(".article-content")){this.check=function(D){var C=D.href.toLowerCase();
return(C.indexOf("http://")!=-1&&C.indexOf(A)==-1)?true:false;};this.set=function(C){C.target="_blank";C.className="external";};var A=window.location.hostname;A=A.replace("www.","").toLowerCase();$$(".article-content a").each(function(C){if(check(C)){set(C);}});}if($$(".multi-feature-carousel")&&$$(".multi-feature-carousel .feature").length>1){$$(".multi-feature-carousel").invoke("slideshow",{transition:1,pause:8000,slidesSelector:".feature",navigation:".multi-feature-carousel-navigation",navStop:true});
$$(".multi-feature-carousel-navigation").invoke("hide");var B=$$(".multi-feature-carousel-navigation a");$$(".multi-feature-carousel .feature .feature-image").each(function(E,D){var C=E.cloneNode(false);$(C).removeClassName("feature-image");B[D].update(C);});$$(".multi-feature-carousel-navigation").invoke("appear",{duration:1});
}if($("multiretailer-mobile_container")&&(isIphone||isBlackberry||isAndroid)){Mobile.init();}if($("single-multiretailer_mobile-container")&&(isIphone||isBlackberry||isAndroid)){Mobile.iphoneScrollTo();}$("searchForm").observe("submit",function(D){var C=WILEY.dummies.util.validateForm("searchForm",true);
if(!C){Event.stop(D);}});};Event.observe(window,"load",_initialize,false);Mobile={iphoneScrollTo:function(){setTimeout(function(){window.scrollTo(0,1);},100);},init:function(){Mobile.iphoneScrollTo();var A=new Array();$$(".multiretailer-product_mobile").each(function(C,B){A.push(C);});if(A.length>1){Mobile.generateMobileLinks(A);
}},generateMobileLinks:function(J){var G,A,B;var E=0;var K=$("next-btn_mobile");var I=$("previous-btn_mobile");var C=K.select("a")[0];var H=I.select("a")[0];var D="/WileyCDA/site/shared/images/mobile-bttn-next-book.png";var F="/WileyCDA/site/shared/images/mobile-bttn-prev-book.png";Mobile.show(K);Event.observe(K,"click",function(L){G=C.title;
A=parseInt(G)+1;B=parseInt(G)-1;if(E>=0){Mobile.show(I);K.select("img")[0].src=D;}Mobile.hide(J[E]);Mobile.show(J[G]);C.title=A;H.title=B;E=G;if(A==J.length){Mobile.hide(K);I.select("img")[0].src=F;}Event.stop(L);});Event.observe(I,"click",function(L){G=H.title;A=parseInt(G)+1;B=parseInt(G)-1;if(E==J.length-1){Mobile.show(K);
I.select("img")[0].src=F;}Mobile.hide(J[E]);Mobile.show(J[G]);C.title=A;H.title=B;E=G;if(B<0){Mobile.hide(I);K.select("img")[0].src=D;}Event.stop(L);});},show:function(A){A.removeClassName("hide");A.addClassName("show");},hide:function(A){A.removeClassName("show");A.addClassName("hide");}};_closeOpenMenu=function(){$(activeMenuId).style.display="none";
};var activeMenuId="";_attachListeners=function(){$$(".expander").each(function(B,A){Event.observe(B,"click",function(C){if($("submenu_"+A).style.display=="none"){if(activeMenuId!=""&&activeMenuId!="submenu_"+A){_closeOpenMenu();activeMenuId="";}Effect.Appear("submenu_"+A,{duration:0.2});_toggleOpacity("show",B.parentNode);
activeMenuId="submenu_"+A;}else{Effect.Fade("submenu_"+A,{duration:0.2});_toggleOpacity("hide",B.parentNode);activeMenuId="";}Event.stop(C);});});Event.observe(document.body,"click",_bodyClick);};_toggleOpacity=function(A,B){if(A=="hide"){B.style.zIndex="0";}else{B.style.zIndex="200";}};_bodyClick=function(A){if(!$(A.element()).up(".menu")&&activeMenuId!=""){_closeOpenMenu();
var B=$(activeMenuId).parentNode;_toggleOpacity("hide",B);}};_cutOffSubmenuTxt=function(){var A;$$("ul.submenu li a").each(function(B){if(B.innerHTML.length>35){A=B.innerHTML.substr(0,32).concat("...");B.innerHTML=A;}});};WILEY.dummies.util.openMe=function(A){new Effect.Appear(A,{duration:0.3});};WILEY.dummies.util.closeMe=function(A){new Effect.Fade(A,{duration:0.3});
};WILEY.dummies.util.goToURL=function(A){if(A){location.href=A;}};WILEY.dummies.util.listExpansionControl=function(C,B,A){if(C=="show"){$(B).hide();$(A).show();}else{$(A).hide();$(B).show();}};WILEY.dummies.util.modalAlert=function(E,C,A,B){var D='<div class="titlebar"><a href="#" onclick="return false;">'+'<img class="closeModal" src="http://media.wiley.com/assets/1547/15/icon_modal-close.gif" alt="close" /></a></div>'+'<div class="title">'+E+'</div><div class="content">'+C+"</div>";
Control.Window.open(D,{width:B,height:A,fade:true,iframeshim:false,closeOnClick:true,className:"modal-alert"});};_showLightbox=function(E){var B='<div class="titlebar"><a href="#" onclick="return false;">'+'<img class="closeModal" src="http://media.wiley.com/assets/1547/15/icon_modal-close.gif" alt="close" /></a></div>'+'<div class="title">'+E.title+'</div><div class="content"><img src="'+E.href+'" alt="" /></div>';
var C=E.href.toQueryParams();var A=parseInt(C.h)+90;var D=parseInt(C.w)+40;Control.Window.open(B,{className:"lightbox",fade:true,height:A,width:D,iframeshim:false,closeOnClick:true});};_createTooltip=function(A){new Control.Window(A,{position:"relative",hover:true,offsetTop:15,offsetLeft:0,width:250,iframeshim:false,className:"tooltip"});
};WILEY.dummies.util.FormValidation={validText:function(E,A,D,B){var C="";if(A&&((B==null)||(B==""))){C+="<li><span>"+E+" is empty</span></li>";}return C;},validSelect:function(E,A,D,C){var B="";if(A&&((C==null)||(C==""))){B+="<li><span>"+E+" is not selected</span></li>";}return B;},validNumber:function(E,A,D,C){var B="";
if(A&&((C==null)||(C==""))){B+="<li><span>"+E+" is empty</span></li>";}return B;},validEmail:function(E,A,D,C){var B=WILEY.dummies.util.FormValidation.validText(E,A,D,C);if(C!=null&&C!=""){if(C.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/)==-1){B+="<li><span>"+E+" is invalid</span></li>";
}}return B;}};WILEY.dummies.util.validateForm=function(C){var D="";var A=arguments[1]||false;$$("form#"+C+" .required").each(function(E){if(E.className.match("text")){D+=WILEY.dummies.util.FormValidation.validText(E.title,true,200,E.value);}if(E.className.match("select")){D+=WILEY.dummies.util.FormValidation.validSelect(E.title,true,200,E.value);
}if(E.className.match("email")){D+=WILEY.dummies.util.FormValidation.validEmail(E.title,true,100,E.value);}});if(D!=""){var B="Please fix the following errors";D="<ul>"+D+"</ul>";WILEY.dummies.util.modalAlert(B,D,"200","350");return false;}if(A){return true;}else{$(C).submit();}};
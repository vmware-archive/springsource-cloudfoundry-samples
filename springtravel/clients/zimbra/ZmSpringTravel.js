/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Constructor.
 * 
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function ZmSpringTravel() {
}

ZmSpringTravel.prototype = new ZmZimletBase();
ZmSpringTravel.prototype.constructor = ZmSpringTravel;

ZmSpringTravel.userData =    {
    "user":{ "username":"josh" },
    "checkinDate":1333004400000,
    "hotel":{  "id":24  },
    "checkoutDate":1329980400000,
    "creditCard":"2422242224222422",
    "smoking":false,
    "beds":1,
    "creditCardName":"Josh long",
    "creditCardExpiryMonth":1,
    "creditCardExpiryYear":5 

    }


ZmSpringTravel.prototype._bookHotelBtnHandler = function() {
	ZmSpringTravel.userData.hotel.id = this._selectedHotelId;
  var postData = JSON.stringify(ZmSpringTravel.userData);
  AjxRpc.invoke(postData, this.getResource("proxySpringTravel.jsp"), {"Content-length": postData.length}, new AjxCallback(this, this._handleHotelBookingPost), false);
  this.pbDialog.popdown();
};

ZmSpringTravel.prototype._handleHotelBookingPost = function(response) {
	var jsonObj = {};
	try{
		jsonObj = eval("("+response.text+")");
	}catch(e) {
		jsonObj = {error: response.text};
	}
	this._confirmHotelBooking(jsonObj);
};

ZmSpringTravel.prototype._confirmHotelBooking = function(bookingResponse) {
	var content = "";
	if(bookingResponse.error) {
		content = this.getMessage("couldNotBookHotel") + bookingResponse.error;
	} else {
		content = AjxMessageFormat.format(this.getMessage("confirmedAndInsertedToCalendar"),[bookingResponse.hotel.name, bookingResponse.id]);
	}	
	var dlg = appCtxt.getMsgDialog();
	dlg.reset();
	dlg.setTitle(this.getMessage("label"));
	dlg.setContent(content);
	dlg.popup();
	this._searchHotelHandler(bookingResponse);	
};

    
ZmSpringTravel.prototype._searchHotels = function() {
	var q = document.getElementById("ZmSpringTravelZimlet_searchField").value;
	var price = 4000;
	var params = ["q=", AjxStringUtil.urlComponentEncode(q), "&price=",AjxStringUtil.urlComponentEncode(price)].join("");
	var jspUrl = [ this.getResource("proxySpringTravel.jsp"), "?", params].join("");
	
	AjxRpc.invoke(null, jspUrl, null, new AjxCallback(this, this._searchHotelsHandler), true);
};

ZmSpringTravel.prototype._searchHotelsHandler = function(response) {
	var jsonObj = {};
	try{
		jsonObj = eval("("+response.text+")");
	}catch(e) {
		jsonObj = {error: response.text};
	}
	if(!jsonObj.error  && jsonObj.hotels) {
		this._setSearchResult(jsonObj.hotels);
	} else {
		this._setSearchResult([]);
	}
};

ZmSpringTravel.prototype._showDialog = function() {
	this._rowIdPrefix = "ZmSpringTravel_row_id_";	
	this._radioIdPrefix = "ZmSpringTravel_radio_id_";
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();//simply popup the dialog
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.setSize("700", "250");
	this.pView.getHtmlElement().innerHTML = this._getDialogView();

	var bookBtnId = Dwt.getNextId();
	var bookBtnDescriptor = new DwtDialog_ButtonDescriptor(bookBtnId, (this.getMessage("bookHotel")), DwtDialog.ALIGN_RIGHT);
	this.pbDialog = this._createDialog({title:this.getMessage("searchForHotels"), view:this.pView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[bookBtnDescriptor]});

this.pbDialog.setButtonListener(bookBtnId, new AjxListener(this, this._bookHotelBtnHandler));

	this._addDialogListeners();
	this._addDialogButtons();
	this._setSearchResult();
	this.pbDialog.popup();
};

ZmSpringTravel.prototype._addDialogListeners = function() {
	document.getElementById("ZmSpringTravelZimlet_searchReultsDiv").onclick =  AjxCallback.simpleClosure(this._handleSearchItemSelect, this);
	
};

ZmSpringTravel.prototype._addDialogButtons = function() {
	var btn = new DwtButton({parent:this.getShell()});
	btn.setText("Search");
	btn.setImage("Search");
	btn.addSelectionListener(new AjxListener(this, this._searchHotels));
	document.getElementById("ZmSpringTravelZimlet_SearchBtnDiv").appendChild(btn.getHtmlElement());
};



ZmSpringTravel.prototype._handleSearchItemSelect =
function(ev) {
	if (!ev){
		var ev = window.event;
	}
	var dwtev = DwtShell.mouseEvent;
	dwtev.setFromDhtmlEvent(ev);
	var el = dwtev.target;
	while (el && el.id.indexOf(this._rowIdPrefix) != 0) {
		el = el.parentNode;
	}
	if (el == null) {
		return;	
	}
	this._selectedHotelId = el.id.replace(this._rowIdPrefix, "");
	this._selectRadioBtn(this._selectedHotelId);
	
	var address = this._hotelIdAndFullAddressMap[this._selectedHotelId];
	this._setGoogleMapImage([address]);
};

ZmSpringTravel.prototype._selectRadioBtn = function(hotelId) {
	var radioId = this._radioIdPrefix+hotelId;
	var radioBtn = document.getElementById(radioId);
	if(radioBtn) {
		radioBtn.checked = true;
	}		
};

ZmSpringTravel.prototype._getDialogView = function() {
	var html = [];
	html.push("<div><table align=center>",
	"<tr>",
	"<td><input id='ZmSpringTravelZimlet_searchField' type=text style='width:250px'></input></td><td id='ZmSpringTravelZimlet_SearchBtnDiv'></td></tr>",
	"</table></div>",
	"<table><tr><td width=50%><div id='ZmSpringTravelZimlet_searchReultsDiv' style='height:200px;overflow:auto'></div></td><td><div><img id='ZmSpringTravelZimlet_GoogleMapsImageId' style='display:none;' width=345px height=200px></img></div></td></tr></table>");
	
	return html.join("");
};



ZmSpringTravel.prototype._setSearchResult = function(hotels) {
	var html = this._getSearchListHtml(hotels);
	document.getElementById("ZmSpringTravelZimlet_searchReultsDiv").innerHTML = html;
	this._setGoogleMapImage(this._hotelIdAndFullAddressMap);
};

ZmSpringTravel.prototype._setGoogleMapImage = function(hotels) {
	var index = 1;
	var showMap = false;
	var markers = [];
	for(var hotelId in hotels) {
		showMap = true;
		markers.push(this._getMapMarker(hotels[hotelId], index++));
	}
	var url = "http://maps.google.com/maps/api/staticmap?size=345x245&sensor=false";
	url = [url, "&", markers.join("&")].join("");
	
	var el = document.getElementById("ZmSpringTravelZimlet_GoogleMapsImageId");
	if(showMap) {
		el.style.display = "block";
		el.style.backgroundImage = "url("+url+")";
	} else {
		el.style.display = "none";
	}
	 
};

ZmSpringTravel.prototype._getGooglImgUrl = function(hotelAddress, label) {
	var url = "http://maps.google.com/maps/api/staticmap?size=345x245&sensor=false";
	return [url, "&", this._getMapMarker(hotelAddress, label)].join("");
};

ZmSpringTravel.prototype._getMapMarker = function(hotelAddress, label) {
	if(!label){
		label = "hotel";
	}
	var address = hotelAddress.replace(/\s/g,"+");
	return ["markers=label:",label,"|color:blue|", address].join("");

};

ZmSpringTravel.prototype._getSearchListHtml = function(hotels) {
	if(!hotels || !(hotels instanceof Array)) {
		hotels = [];
	}
	this._hotelIdAndFullAddressMap = [];
	var html = [];
	html.push("<table RULES=ROWS FRAME=BOX width=100% align=center>");
	for(var i=0; i< hotels.length; i++) {
		var h = hotels[i];
		var fullAddr = [h.address," ",h.city, " ",h.state," " ,h.country].join("");
		var rowId = this._rowIdPrefix +h.id;
		var radioId = this._radioIdPrefix + h.id;
		html.push("<tr class='SpringTravelZimlet_row' id=\"",rowId,"\"><td width='6px'>",i+1,"</td><td width='4px'><input type=radio id=\"",radioId,"\" name='ZmSpringTravel_radio'></input></td><td><label style='font-weight:bold;font-size:14px'>"
		,h.name,"</label><br><label style='font-size:10px;font-color:gray'>",fullAddr,"</label>",
		"<br><label style='font-size:12px;font-color:blue'>Price: $",h.price,"</label>","</td></tr>");
		this._hotelIdAndFullAddressMap[h.id] = fullAddr;
	}
	html.push("</table>");

	
	return html.join("");
	/*
	var html = [];

	var subs = {
		version : appCtxt.getSettings().getInfoResponse.version,
		userAgent : [ this.getMessage("userAgent"), " ", navigator.userAgent ]
				.join(""),
		copyright : this.getMessage("copyright")
	};
	return AjxTemplate.expand(
			"com_zimbra_about.templates.ZmSpringTravel#DialogView", subs);
	*/		

};


ZmSpringTravel.prototype.initializeToolbar = function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("APPT") >= 0) {
		this._addSpringTravelToolbarBtn(toolbar, controller);
	}
};

ZmSpringTravel.prototype._addSpringTravelToolbarBtn = function(toolbar, controller) {
	if (!toolbar.getButton("SPRING_TRAVEL_BOOK_HOTEL")) {
		ZmMsg.springTravelBookHotel = this.getMessage("bookHotel");
		for (var i = 0; i < toolbar.opList.length; i++) {
			if (toolbar.opList[i] == "COMPOSE_FORMAT" || toolbar.opList[i] == "VIEW_MENU") {
				buttonIndex = i + 1;
				break;
			}
		}
		var btn = toolbar.createOp("SPRING_TRAVEL_BOOK_HOTEL", {image:"Search", text:this.getMessage("searchHotel"), tooltip:this.getMessage("searchHotel"), index:buttonIndex});
		var buttonIndex = 0;
		toolbar.addOp("SPRING_TRAVEL_BOOK_HOTEL", buttonIndex);
		this._composerCtrl = controller;
		this._composerCtrl._ZmSpringTravelZimlet = this;
		btn.addSelectionListener(new AjxListener(this, this._showDialog));
	}
};


ZmSpringTravel.prototype._searchHotelHandler = function(bookingResponse) {
	var editorType = "HTML";
	var composeView = this._composerCtrl._composeView;
	if (composeView.getComposeMode() != "text/html") {
		editorType = "PLAIN_TEXT";
	}
	var currentContent = composeView.getHtmlEditor().getContent();
	if (editorType == "HTML") {
		var lastIndx = currentContent.lastIndexOf("</body></html>");
		var tmp = currentContent.substr(0, lastIndx);
		var hotelUrl = this._getGooglImgUrl(this._hotelIdAndFullAddressMap[bookingResponse.hotel.id], "hotel");

		var htmlstr = ["<b>",this.getMessage("hotelDetails"),"</b>",this._getHotelConfirmationHtml(editorType, bookingResponse),
		 "<br><img width=345px height=200px src=\"",
		hotelUrl, "\"></img>"].join("");
		
		var newContent = [tmp, htmlstr, "</body></html>"].join("");
	} else {
		var newContent = [currentContent, this.getMessage("hotelDetails"), "\n", this._getHotelConfirmationHtml(editorType, bookingResponse)].join("");
	}	
	composeView.getHtmlEditor().setContent(newContent);		
};

ZmSpringTravel.prototype._getHotelConfirmationHtml = function(editorType, bookingResponse) {
	var html = [];
	var h = bookingResponse.hotel;
	bookingResponse["User Name"] = bookingResponse.user.name;
	bookingResponse["Hotel Name"] = h.name;
	bookingResponse["Hotel Address"] =  [h.address," ",h.city, " ",h.state," " ,h.country].join("");
	for(var item in bookingResponse) {
		if(item == "description" || item == "user" || item == "hotel" || item == "creditcard") {
			continue;
		}
		if(editorType =="HTML") {
			html.push("<b>", item, ": </b>", bookingResponse[item], "<br>");
		} else {
			html.push(item, ": ", bookingResponse[item], "\n");
		}
	}
	return html.join("");
};

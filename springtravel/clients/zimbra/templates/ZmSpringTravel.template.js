AjxTemplate.register("com_zimbra_springtravel.templates.ZmSpringTravel#DialogView", 
function(name, params, data, buffer) {
	var _hasBuffer = Boolean(buffer);
	data = (typeof data == "string" ? { id: data } : data) || {};
	buffer = buffer || [];
	var _i = buffer.length;

	buffer[_i++] = "<div class='AboutZimlet_background'><table><tr><td align=center><div class='ImgAppBanner' style='width:220px'></div></td></tr><tr><td><input readonly='readonly' type=text value='";
	buffer[_i++] = data["version"];
	buffer[_i++] = "' class='AboutZimlet_input'></input></td></tr><tr><td><textarea type=text spellcheck='false' readonly='readonly' class='AboutZimlet_textarea'>";
	buffer[_i++] = data["userAgent"];
	buffer[_i++] = "</textarea></td></tr><tr/><tr><td><textarea type=text spellcheck='false' readonly='readonly' class='AboutZimlet_textarea'>";
	buffer[_i++] = data["copyright"];
	buffer[_i++] = "</textarea></td></tr></table></div>";

	return _hasBuffer ? buffer.length : buffer.join("");
},
{
	"id": "DialogView"
}, true);
AjxPackage.define("com_zimbra_springtravel.templates.ZmSpringTravel");
AjxTemplate.register("com_zimbra_springtravel.templates.ZmSpringTravel", AjxTemplate.getTemplate("com_zimbra_springtravel.templates.ZmSpringTravel#DialogView"), AjxTemplate.getParams("com_zimbra_springtravel.templates.ZmSpringTravel#DialogView"));


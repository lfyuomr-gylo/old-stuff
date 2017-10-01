function timeLua(code) {
	var start = Date.now();
	runLuaCode(code);
	var time = Date.now() - start;
	return Date.now() - start;
}

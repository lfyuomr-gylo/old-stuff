if [ ! -f emlua.js ];
then
	make generic
	emcc runluacode.c liblua.a -s EXPORTED_FUNCTIONS='["_runLuaCode"]' -s TOTAL_MEMORY=104857600 -s TOTAL_STACK=104857600 -o emlua.js
	cat wrap.js >> emlua.js
fi

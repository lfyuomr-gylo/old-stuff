#include "lua.h"
#include "lauxlib.h"
#include "lualib.h"
#include <string.h>

void runLuaCode(const char *buff) {
    lua_State *L = luaL_newstate();
    luaL_openlibs(L);
    luaL_loadbuffer(L, buff, strlen(buff), "argv[1] lol");
    lua_pcall(L, 0, 0, 0);
    lua_close(L);
}

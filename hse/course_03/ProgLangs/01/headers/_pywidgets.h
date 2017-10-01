#include <Python.h>

#include "widgets.h"


static PyObject *_pywidgets_Object_GetClassName(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Object_Delete(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Application_New(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Application_Exec(PyObject *self, PyObject *args);
static PyObject *_pywidgets_VBoxLayout_New(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Layout_AddWidget(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Widget_New(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Widget_SetVisible(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Widget_SetWindowTitle(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Widget_SetLayout(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Widget_SetSize(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Label_New(PyObject *self, PyObject *args);
static PyObject *_pywidgets_Label_SetText(PyObject *self, PyObject *args);
static PyObject *_pywidgets_PushButton_New(PyObject *self, PyObject *args);
static PyObject *_pywidgets_PushButton_SetText(PyObject *self, PyObject *args);
static PyObject *_pywidgets_PushButton_SetOnClickedStaticHandler(PyObject *self, PyObject *args);
static PyObject *_pywidgets_PushButton_RegisterOnClicked(PyObject *self, PyObject *args);

static PyMethodDef _pywidgets_methods[] = {
        {"Object_GetClassName", _pywidgets_Object_GetClassName, METH_VARARGS, ""},
        {"Object_Delete", _pywidgets_Object_Delete, METH_VARARGS, ""},
        {"Application_New", _pywidgets_Application_New, METH_VARARGS, ""},
        {"Application_Exec", _pywidgets_Application_Exec, METH_VARARGS, ""},
        {"VBoxLayout_New", _pywidgets_VBoxLayout_New, METH_VARARGS, ""},
        {"Layout_AddWidget", _pywidgets_Layout_AddWidget, METH_VARARGS, ""},
        {"Widget_New", _pywidgets_Widget_New, METH_VARARGS, ""},
        {"Widget_SetVisible", _pywidgets_Widget_SetVisible, METH_VARARGS, ""},
        {"Widget_SetWindowTitle", _pywidgets_Widget_SetWindowTitle, METH_VARARGS, ""},
        {"Widget_SetLayout", _pywidgets_Widget_SetLayout, METH_VARARGS, ""},
        {"Widget_SetSize", _pywidgets_Widget_SetSize, METH_VARARGS, ""},
        {"Label_New", _pywidgets_Label_New, METH_VARARGS, ""},
        {"Label_SetText", _pywidgets_Label_SetText, METH_VARARGS, ""},
        {"PushButton_New", _pywidgets_PushButton_New, METH_VARARGS, ""},
        {"PushButton_SetText", _pywidgets_PushButton_SetText, METH_VARARGS, ""},
        {"PushButton_SetOnClickedStaticHandler", _pywidgets_PushButton_SetOnClickedStaticHandler, METH_VARARGS, ""},
        {"PushButton_RegisterOnClicked", _pywidgets_PushButton_RegisterOnClicked, METH_VARARGS, ""},
        {NULL, NULL, 0, NULL}
};

static PyModuleDef _pywidgets_module = {
        PyModuleDef_HEAD_INIT,
        "_pywidgets", // module name
        NULL,        // module dicumentation
        -1,         // size of per-interpreter state of the module, or -1 if the module keeps state in global variables
        _pywidgets_methods
};

PyMODINIT_FUNC PyInit__pywidgets(void) {
    return PyModule_Create(&_pywidgets_module);
}
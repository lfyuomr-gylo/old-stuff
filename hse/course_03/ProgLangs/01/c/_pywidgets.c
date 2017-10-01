#include <_pywidgets.h>

void *pointerFromTupleLongElement(PyObject *tuple, Py_ssize_t index) {
    PyObject *obj = PyTuple_GetItem(tuple, index);
    if (obj) {
        if (PyLong_Check(obj)) {
            return PyLong_AsVoidPtr(obj);
        }
    }

    return NULL;
}

static PyObject *_pywidgets_Object_GetClassName(PyObject *self, PyObject *args) {
    struct Object *obj = pointerFromTupleLongElement(args, 0);
    if (!obj) {
        return NULL;
    }
    const char *className = Object_GetClassName(obj);
    return PyUnicode_FromString(className);
}

static PyObject *_pywidgets_Object_Delete(PyObject *self, PyObject *args) {
    struct Object *obj = pointerFromTupleLongElement(args, 0);
    if (!obj) {
        return NULL;
    }

    Object_Delete(obj);
    Py_RETURN_NONE;
}

static PyObject *_pywidgets_Application_New(PyObject *self, PyObject *args) {
    struct Application *app = Application_New();
    return PyLong_FromVoidPtr(app);
}

static PyObject *_pywidgets_Application_Exec(PyObject *self, PyObject *args) {
    struct Application *app = pointerFromTupleLongElement(args, 0);
    if (!app) {
        return NULL;
    }
    int status = Application_Exec(app);
    return PyLong_FromLong(status);
}

static PyObject *_pywidgets_VBoxLayout_New(PyObject *self, PyObject *args) {
    struct Object *parent = pointerFromTupleLongElement(args, 0);
    if (!parent) {
        return NULL;
    }
    struct VBoxLayout *vbox = VBoxLayout_New((struct Widget *) parent);
    return PyLong_FromVoidPtr(vbox);
}

static PyObject *_pywidgets_Layout_AddWidget(PyObject *self, PyObject *args) {
    struct Layout *layout = pointerFromTupleLongElement(args, 0);
    struct Widget *widget = pointerFromTupleLongElement(args, 1);
    if (!layout || !widget) {
        return NULL;
    }

    Layout_AddWidget(layout, widget);
    Py_RETURN_NONE;
}

static PyObject *_pywidgets_Widget_New(PyObject *self, PyObject *args) {
    struct Object *parent = PyTuple_Size(args) ? pointerFromTupleLongElement(args, 0) : NULL;

    struct Widget *widget = Widget_New((struct Widget *) parent);
    return PyLong_FromVoidPtr(widget);
}

static PyObject *_pywidgets_Widget_SetVisible(PyObject *self, PyObject *args) {
    struct Widget *widget = pointerFromTupleLongElement(args, 0);
    PyObject *visibilityObject = PyTuple_GetItem(args, 1);
    if (!widget || !visibilityObject || !PyBool_Check(visibilityObject)) {
        return NULL;
    }
    bool visibility = (bool) PyObject_IsTrue(visibilityObject);

    Widget_SetVisible(widget, visibility);

    Py_RETURN_NONE;
}

static PyObject *_pywidgets_Widget_SetWindowTitle(PyObject *self, PyObject *args) {
    struct Widget *widget = pointerFromTupleLongElement(args, 0);
    PyObject *titleObject = PyTuple_GetItem(args, 1);
    if (!widget || !titleObject || !PyUnicode_Check(titleObject)) {
        return NULL;
    }

    const char *title = PyUnicode_AsUTF8(titleObject);
    Widget_SetWindowTitle(widget, title);
    Py_RETURN_NONE;
}

static PyObject *_pywidgets_Widget_SetLayout(PyObject *self, PyObject *args) {
    struct Widget *widget = pointerFromTupleLongElement(args, 0);
    struct Layout *layout = pointerFromTupleLongElement(args, 1);
    if (!widget || !layout) {
        return NULL;
    }

    Widget_SetLayout(widget, layout);
    Py_RETURN_NONE;
}

static PyObject *_pywidgets_Widget_SetSize(PyObject *self, PyObject *args) {
    struct Widget *widget = pointerFromTupleLongElement(args, 0);
    PyObject *widthObject = PyTuple_GetItem(args, 1);
    PyObject *heightObject = PyTuple_GetItem(args, 2);
    if (!widget || !widthObject || !heightObject || !PyLong_Check(widthObject) || !PyLong_Check(heightObject)) {
        return NULL;
    }

    long width = PyLong_AsLong(widthObject);
    long height = PyLong_AsLong(heightObject);

    Widget_SetSize(widget, (int) width, (int) height);
    Py_RETURN_NONE;
}

static PyObject *_pywidgets_Label_New(PyObject *self, PyObject *args) {
    struct Object *parent = pointerFromTupleLongElement(args, 0);
    if (!parent) {
        return NULL;
    }

    struct Label *label = Label_New((struct Widget *) parent);
    return PyLong_FromVoidPtr(label);
}

static PyObject *_pywidgets_Label_SetText(PyObject *self, PyObject *args) {
    struct Label *label = pointerFromTupleLongElement(args, 0);
    PyObject *textObject = PyTuple_GetItem(args, 1);
    if (!label || !textObject || !PyUnicode_Check(textObject)) {
        return NULL;
    }
    const char *text = PyUnicode_AsUTF8(textObject);

    Label_SetText(label, text);

    Py_RETURN_NONE;
}

static PyObject *_pywidgets_PushButton_New(PyObject *self, PyObject *args) {
    struct Widget *parent = pointerFromTupleLongElement(args, 0);
    if (!parent) {
        return NULL;
    }

    struct PushButton *button = PushButton_New(parent);
    return PyLong_FromVoidPtr(button);
}

static PyObject *_pywidgets_PushButton_SetText(PyObject *self, PyObject *args) {
    struct PushButton *button = pointerFromTupleLongElement(args, 0);
    PyObject *textObject = PyTuple_GetItem(args, 1);
    if (!button || !textObject || !PyUnicode_Check(textObject)) {
        return NULL;
    }
    const char *text = PyUnicode_AsUTF8(textObject);

    PushButton_SetText(button, text);

    Py_RETURN_NONE;
}

static PyObject *pushButtonOnClickedHandler;

static PyObject *_pywidgets_PushButton_SetOnClickedStaticHandler(PyObject *self, PyObject *args) {
    PyObject *callObject = PyTuple_GetItem(args, 0);
    if (!callObject || !PyCallable_Check(callObject)) {
        return NULL;
    }
    pushButtonOnClickedHandler = callObject;

    Py_RETURN_NONE;
}

void onClicked(struct Object *obj) {
    PyObject_Call(pushButtonOnClickedHandler, PyTuple_Pack(1, PyLong_FromVoidPtr(obj)), NULL);
}

static PyObject *_pywidgets_PushButton_RegisterOnClicked(PyObject *self, PyObject *args) {
    struct PushButton *button = pointerFromTupleLongElement(args, 0);
    if (!button) {
        return NULL;
    }

    PushButton_SetOnClicked(button, onClicked);

    Py_RETURN_NONE;
}
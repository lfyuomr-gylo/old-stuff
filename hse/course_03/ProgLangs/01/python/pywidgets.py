from _pywidgets import *

_pointers = {}


_pushButton_onClicked_handlers = {} # PushButton -> receivers

def _pushButton_onClicked_emitter(ptr):
    button = _pointers[ptr]
    for handler in _pushButton_onClicked_handlers[button]:
        handler(button)

PushButton_SetOnClickedStaticHandler(_pushButton_onClicked_emitter)

class Object:
    @property
    def _pointer(self):
        return self.__pointer

    @_pointer.setter
    def _pointer(self, value):
        _pointers[value] = self
        self.__pointer = value

    @_pointer.deleter
    def _pointer(self):
        del _pointers[self.__pointer]
        self.__pointer = None

    _children = []

    def __init__(self):
        raise NotImplementedError("Object is an abstract class")

    def __del__(self):
        if self._pointer:
            Object_Delete(self._pointer)
            del self._pointer

        while len(self._children):
            child = self._children.pop()
            del child._pointer
            del child


    def get_class_name(self):
        return Object_GetClassName(self._pointer)


class Widget(Object):
    def __init__(self, parent=None):
        if parent:
            self._pointer = Widget_New(parent._pointer)
            parent._children.append(self)
        else:
            self._pointer = Widget_New()

    def set_visible(self, v):
        Widget_SetVisible(self._pointer, v)

    def set_window_title(self, title):
        Widget_SetWindowTitle(self._pointer, title)

    def set_layout(self, layout):
        Widget_SetLayout(self._pointer, layout._pointer)

    def set_size(self, w, h):
        Widget_SetSize(self._pointer, w, h)

class PushButton(Widget):
    def __init__(self, parent=None):
        self._pointer = PushButton_New(parent._pointer)
        parent._children.append(self)

        PushButton_RegisterOnClicked(self._pointer)

    def __del__(self):
        Widget.__del__(self)
        _pushButton_onClicked_handlers.pop(self, None)

    def set_text(self, text):
        PushButton_SetText(self._pointer, text)

    def set_on_clicked(self, callback):
        _pushButton_onClicked_handlers[self] = _pushButton_onClicked_handlers.get(self, []) + [callback]


class Label(Widget):
    def __init__(self, parent):
        self._pointer = Label_New(parent._pointer)
        parent._children.append(self)

    def set_text(self, text):
        Label_SetText(self._pointer, text)


class Application(Object):
    def __init__(self):
        self._pointer = Application_New()

    def exec(self):
        return Application_Exec(self._pointer)


class Layout(Object):
    def __init__(self, parent):
        self._pointer = Layout_New(parent._pointer)
        parent._children.append(self)

    def add_widget(self, widget):
        Layout_AddWidget(self._pointer, widget._pointer)


class VBoxLayout(Layout):
    def __init__(self, parent):
        self._pointer = VBoxLayout_New(parent._pointer)
        parent._children.append(self)

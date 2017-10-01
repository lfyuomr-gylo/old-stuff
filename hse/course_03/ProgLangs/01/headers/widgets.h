#ifndef MYQTBINDING_MY_QT_H
#define MYQTBINDING_MY_QT_H

#include <stdbool.h>

#ifdef __cplusplus
#define MY_EXTERN
#else
#define MY_EXTERN extern
#endif // __cplusplus

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

struct Object;
struct Widget /* extends Object */;
struct PushButton /* extends Widget */;
struct Label /* extends Widget */;
struct Application /* extends Object */;
struct Layout /* extends Object */;
struct VBoxLayout /* extends Layout */;

typedef void (NoArgumentsCallback)(struct Object *sender);

MY_EXTERN const char* Object_GetClassName(struct Object *object);
MY_EXTERN void Object_Delete(struct Object *object);

MY_EXTERN struct Application* Application_New();
MY_EXTERN int Application_Exec(struct Application *app);

MY_EXTERN struct VBoxLayout* VBoxLayout_New(struct Widget *parent);
MY_EXTERN void Layout_AddWidget(struct Layout *layout, struct Widget *widget);

MY_EXTERN struct Widget* Widget_New(struct Widget *parent);

MY_EXTERN void Widget_SetVisible(struct Widget *widget, bool v);
MY_EXTERN void Widget_SetWindowTitle(struct Widget *widget, const char *title);
MY_EXTERN void Widget_SetLayout(struct Widget *widget, struct Layout *layout);
MY_EXTERN void Widget_SetSize(struct Widget *widget, int w, int h);

MY_EXTERN struct Label* Label_New(struct Widget *parent);
MY_EXTERN void Label_SetText(struct Label *label, const char *text);

MY_EXTERN struct PushButton* PushButton_New(struct Widget *parent);
MY_EXTERN void PushButton_SetText(struct PushButton *button, const char *text);
MY_EXTERN void PushButton_SetOnClicked(struct PushButton *button, NoArgumentsCallback *callback);

#ifdef __cplusplus
};
#endif // __cplusplus


#endif //MYQTBINDING_MY_QT_H

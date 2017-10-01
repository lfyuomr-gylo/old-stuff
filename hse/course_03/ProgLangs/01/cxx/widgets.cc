#include <QObject>
#include <QApplication>

#include <widgets.h>
#include <QtWidgets/QWidget>
#include <QtWidgets/QVBoxLayout>
#include <QtWidgets/QLabel>
#include <QtWidgets/QPushButton>

struct Object{};
struct Widget /* extends Object */{};
struct PushButton /* extends Widget */{};
struct Label /* extends Widget */{};
struct Application /* extends Object */{};
struct Layout /* extends Object */{};
struct VBoxLayout /* extends Layout */{};

const char* Object_GetClassName(struct Object *object) {
    return reinterpret_cast<QObject*>(object)->metaObject()->className() + 1; // + 1 cause we don't want to expose 'Q' letter
}
void Object_Delete(struct Object *object) {
    delete reinterpret_cast<QObject *>(object);
}

static bool __my_application_created = false;
static int __my_application_fake_argc = 1;
static char *__my_application_fake_argv = new char[17];

struct Application* Application_New() {
    if (!__my_application_created) {
        strcpy(__my_application_fake_argv, "python_extension");
        __my_application_created = true;
    } else {
        // TODO: generate something like exception
    }

    return reinterpret_cast<Application*>(new QApplication(__my_application_fake_argc, &__my_application_fake_argv));
}
int Application_Exec(struct Application *app) {
    return reinterpret_cast<QApplication*>(app)->exec();
}

struct VBoxLayout* VBoxLayout_New(struct Widget *parent) {
    QWidget *qParent = reinterpret_cast<QWidget *>(parent);
    return reinterpret_cast<VBoxLayout *>(new QVBoxLayout(qParent));
}
void Layout_AddWidget(struct Layout *layout, struct Widget *widget) {
    QLayout *qLayout = reinterpret_cast<QLayout *>(layout);
    QWidget *qWidget = reinterpret_cast<QWidget *>(widget);
    qLayout->addWidget(qWidget);
}

struct Widget* Widget_New(struct Widget *parent) {
    QWidget *qParent = reinterpret_cast<QWidget *>(parent);
    return reinterpret_cast<Widget *>(new QWidget(qParent));
}

void Widget_SetVisible(struct Widget *widget, bool v) {
    QWidget *qWidget = reinterpret_cast<QWidget *>(widget);
    qWidget->setVisible(v);
}
void Widget_SetWindowTitle(struct Widget *widget, const char *title) {
    QWidget *qWidget = reinterpret_cast<QWidget *>(widget);
    const QString qTitle = QString::fromUtf8(title);
    qWidget->setWindowTitle(qTitle);
}
void Widget_SetLayout(struct Widget *widget, struct Layout *layout) {
    QLayout *qLayout = reinterpret_cast<QLayout *>(layout);
    QWidget *qWidget = reinterpret_cast<QWidget *>(widget);
    qWidget->setLayout(qLayout);
}
void Widget_SetSize(struct Widget *widget, int w, int h) {
    QWidget *qWidget = reinterpret_cast<QWidget *>(widget);
    qWidget->resize(w, h);
}

struct Label* Label_New(struct Widget *parent) {
    QWidget *qParent = reinterpret_cast<QWidget *>(parent);
    return reinterpret_cast<Label *>(new QLabel(qParent));
}
void Label_SetText(struct Label *label, const char *text) {
    QLabel *qLabel = reinterpret_cast<QLabel *>(label);
    const QString qText = QString::fromUtf8(text);
    qLabel->setText(qText);
}

struct PushButton* PushButton_New(struct Widget *parent) {
    QWidget *qParent = reinterpret_cast<QWidget *>(parent);
    return reinterpret_cast<PushButton *>(new QPushButton(qParent));
}
void PushButton_SetText(struct PushButton *button, const char *text) {
    QPushButton *qPushButton = reinterpret_cast<QPushButton *>(button);
    const QString qText = QString::fromUtf8(text);
    qPushButton->setText(qText);
}
void PushButton_SetOnClicked(struct PushButton *button, NoArgumentsCallback *callback) {
    QPushButton *qPushButton = reinterpret_cast<QPushButton *>(button);
    // TODO: think about receiver
    QObject::connect(qPushButton, &QPushButton::clicked,
                     qPushButton, [qPushButton, callback]{callback(reinterpret_cast<Object *>(qPushButton));});
}


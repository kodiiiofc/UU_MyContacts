# Домашнее задание по теме "ContentProvider"

## Приложение «Мои контакты»

Необходимо написать приложение получения списка контактов на основе `RecyclerView`. На элементе
списка контакта расположены два `ImageView`: один для выполнения звонка, второе для отправки
сообщения. По нажатию на эти `ImageView` выполняются вышеописанные действия. При получении списка
контактов, приложение должно запрашивать разрешение на доступ к контактам, перед выполнением
звонка – разрешение на выполнение звонков.

``` XML
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.CALL_PHONE"/>
```

Логика получения разрешений и работы приложения аналогична тематике отработанного занятия, за
исключением, что работу нужно выполнить с применением `RecyclerView` и по нажатию на иконки списка
для вызова действий. Обработку нажатия списков `RecyclerView` мы рассматривали на 37 занятии.

**Усложненный уровень.** По нажатию на `ImageView` отправки сообщений выполняется переход на
следующий экран, на котором отображается номер куда отправляется сообщение, поле ввода этого
сообщения и кнопка отправки сообщения. Возможен запрос разрешений на отправку сообщений перед
переходом на этот экран.

Необходимые разрешения.

``` XML
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.SEND_SMS" />
<dist:module dist:instant="true" />
```

Порядок выполнения отправки сообщений в активити передачи сообщений с использованием `SmsManager`.

``` Kotlin
    try {
            val smsManager:SmsManager
            if (Build.VERSION.SDK_INT>=23) {
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
             smsManager = SmsManager.getDefault()
            }

            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Please enter all the data.."+e.message.toString(),                 Toast.LENGTH_LONG).show()
        }
```

Для работы приложения необходимо создать:

На первом экране:

1. `Toolbar` с заголовком названия приложения.

2. `RecyclerView` списка контактов.

На втором экране:

1. `Toolbar` с заголовком названия приложения и кнопкой возвращения назад к первому экрану.

2. Поле вывода номера телефона адресата сообщения.

3. Поле ввода текста сообщения.

4. Кнопка «Отправить».

Приложение необходимо сохранить проектом в удаленном репозитории, для проверки качества предоставить
ссылку преподавателю, либо сделать скрины эмулятора при каждом шаге работы приложения или снять
видео экрана при работе приложения.

## Видео к домашнему заданию по теме "ContentProvider"

# Hex Editor

Hex Editor - это приложение для просмотра и редактирования файлов в шестнадцатеричном формате.

## Запуск приложения

1. Убедитесь, что у вас установлена Java Development Kit (JDK) версии 8 или выше.
2. Склонируйте репозиторий с помощью команды: git clone https://github.com/Dvimin/hex_editor.git
3. Перейдите в каталог проекта: cd hex_editor
4. Запустите приложение с помощью команды: java -cp FirstTable.Main

## Местонахождение класса Main

Класс `Main` находится в файле `FirstTable/Main.java`.

   Примечание: Если у вас возникнут проблемы с запуском, убедитесь, что у вас установлена версия Java, совместимая с приложением.

## Функционал

### Открытие файла

- Чтобы открыть файл, выберите пункт "Файл" в меню и затем "Открыть".
- В диалоговом окне выберите файл, который вы хотите открыть, и нажмите "Открыть".
- Файл будет отображаться в таблице, где каждый байт представлен в шестнадцатеричном формате.

### Редактирование файла

- Чтобы отредактировать значение байта, дважды щелкните на соответствующую ячейку и введите новое значение.
- Изменения будут автоматически сохраняться в файле.

### Сохранение файла

- Чтобы сохранить изменения в файле, выберите пункт "Файл" в меню и затем "Сохранить".
- Файл будет сохранен с изменениями.

### Дополнительные функции

- Изменить: Изменяет значение выбранной ячейки.
- Сбросить: Сбрасывает значение выбранной ячейки.
- Удалить: Удаляет выбранные ячейки.
- Вставить слева со сдвигом: Вставляет содержимое буфера обмена слева от выбранной ячейки со сдвигом.
- Вставить справа со сдвигом: Вставляет содержимое буфера обмена справа от выбранной ячейки со сдвигом.
- Копировать: Копирует выбранные ячейки в буфер обмена.
- Вырезать со сдвигом: Вырезает выбранные ячейки со сдвигом и помещает их в буфер обмена.
- Вырезать со сбросом: Вырезает выбранные ячейки и помещает их в буфер обмена.
- Вставить без сдвига: Вставляет содержимое буфера обмена без сдвига от выбранной ячейки.
- Очистить данные: Очищает содержимое таблицы.
- Вставить ячейку справа: Вставляет новую ячейку справа от выбранной ячейки.
- Вставить ячейку слева: Вставляет новую ячейку слева от выбранной ячейки.
- Найти: Выполняет поиск значения в таблице по маске и по точному значению.

### Меню

- Файл: Содержит пункты для открытия, сохранения и выхода из приложения.
- Помощь: Содержит пункт для получения помощи.
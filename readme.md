<h1>Программа сервера и клиентов</h1>

<h3>Данная программа запускает сервер, и позволяет нескольким клиентам подключаться к этому серверу.
</h3>

##Программа содержит 3 основных пакета
- **client** хранит в себе сущность клиента и класс, отвечающий за
  чтение данных, вводимых клиентом
- **server** хранит в себе сам сервер
- **sharedResources** хранит в себе вспомогательные классы (считыватели настроек и т д)и файлы
##
# Основные классы и их предназначение:

- Класс **Connection**-класс обертка над Socket, который хранит в себе Socket,ObjectOutputStream и ObjectInputStream. Он
  стерилизует все отправляемые сообщения (Message) в ObjectOutputStream. и при необходимости может их десериализовать.
  Так же класс поддерживает интерфейс Closable для автоматического ого закрытия всех потоков. Структура
  класса:![Connection](https://user-images.githubusercontent.com/65041919/187931805-f041cb7e-b0f7-49ba-bcb9-3cee011c61b2.png)

- Класс **Logger**  - Класс предназначен для логирования информации и от сервера, и от клиентов, в один файл, который
  будет создать автоматически при запуске программы. Так же елси файл уже был создан ранее, при новом запуске программы,
  данные в файле буду сброшены. Это сделано для того, чтобы не писать в один файл результаты работы двух программ. Также
  Logger дублирует и информацию в консоль. Структура класса:![Logger](https://user-images.githubusercontent.com/65041919/187931999-bf33bcd2-618d-4af3-8d32-a0ca2672a6e9.png

- Класс **Message**-Класс предназначенный для создания обёртки над отправлемыемими сообщениями. Содержит в себе тип
  сообщения MessageType и сам текст сообщения. Поддерживает сериализацию и десериализацию Структура
  класса:![Message](https://user-images.githubusercontent.com/65041919/187932725-47354fda-7585-4039-8477-7d4b4736a006.png)

- Enum **MessageType**- специальное перечисление,которое позволяет определить тип отправленных/полученных сообщения от
  сервера и от клиента.
- Класс **SettingReader**-специальный класс, предназначенный для чтения из файла настроек данных для ServerSocket и для
  Socket. Структура класса:![SettingReader](https://user-images.githubusercontent.com/65041919/187932813-914723f1-63a4-44d1-a32f-0cc079098aca.png)

- Класс **Server**-Класс в котором реализована основная логика Сервера. Данный класс отвечает за корректную работу
  сервера, подключение и обслуживание клиентов. Все клиенты обслуживаются в отдельных потоках,и хранятся в специальной
  мапе , которая не позволяет соаздть двух клиентов с одинаковым именем. Структура класса:![Server](https://user-images.githubusercontent.com/65041919/187932852-1f08d3ec-37aa-43a5-8e4d-3ebe89c93623.png)

- Класс **Client**-Класс предоставляющий клиента. Данный класс создает и подключает клиента к нашему серверу ,используя
  настройки в файле sharedResources/serverSettings.property. Все клиенты создаются в отдельных потоках.За это отвечает
  внутренний класс SocketThread Структура класса:![Client](https://user-images.githubusercontent.com/65041919/187932910-35802e61-85be-4aae-817e-5c290eaeb216.png)

- Класс **ClientTextReader** -чтобы не создавать все время поток для
  чтения сообщений клиентами, был создан этот отдельный класс,
  который просто инициализирует Scanner для чтения пользовательских вводов с консоли
  Структура класса:![ClientTextReader](https://user-images.githubusercontent.com/65041919/187932979-6dafe71b-1f17-432b-a80a-95671dc1bc8f.png)

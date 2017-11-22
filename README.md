# TestAuthService
Почему не стала выделять отдельный класс для элементов WebElements:
Если выделить отдельный класс, то придется каждый раз создавать объект этого класса для доступа к элементу, что не очень удобно. 
Поэтому решила использовать pageFactory в каждом классе отдельно. 
Вообще, можно было бы выделить для каждого теста отдельный функционал с объявлением веб элементов, там же прописать логику (как обычно делают люди),
но мне, честно, лениво создавать по два класса на каждую проверку (1-для прописывания логики, 2-непосредственно для использования этой логики для проверки).
Да и, по правде говоря, пока не вижу в этом смысла, т.к. проверки атомарные и не подразумевают каких -то разветвлений (или для чего еще может понадобиться такая архитектура тестов?).
Возможно позже в этом появится необходимость, и я всё-таки переборю лень и сделаю разграничение логики и ее использования в других классах.

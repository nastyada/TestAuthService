package Search;

import HelpClass.BaseClass;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.annotations.Test;

import java.util.List;


@Test
public class SearchAuth extends BaseClass {
    @FindBy(xpath = "//div[@class='ant-select-selection\n" +
            "            ant-select-selection--single']")
    private WebElement menu;
    @FindBy(xpath = "//ul[@class='ant-select-dropdown-menu ant-select-dropdown-menu-vertical  ant-select-dropdown-menu-root']/li")
    private List<WebElement> menuPoint;
    @FindBy(xpath = "//*[@id=\"root\"]/div/div[1]/div/div[1]/div/div[2]/span[1]/span/span/span[2]/input")
    private WebElement searchAreaTB;
    @FindBy(xpath = "//*[@id=\"root\"]//table/tbody/tr")
    private List<WebElement> tableRow;
    @FindBy(xpath = "//*[@id=\"root\"]//table/tbody/tr/td")
    private List<WebElement> tableCol;
    @FindBy(xpath = "//*[@id=\"root\"]//table/thead/tr/th")
    private List<WebElement> tableHeader;
    @FindBy(xpath = "//*[@id=\"authorization\"]/div/div[2]/div[1]/div/span[3]/button")
    private WebElement addBTN;
    @FindBy(xpath = "//div[@class='ant-modal-footer']//button[1]")
    private WebElement cancelBTN;
    // Пояснение почему не сделала декомпозицию: вся логика+проверки в одном методе, что плохо,
    // НО! если разнести на несколько методов, класс растянется в два раза, т.к. все составленные списки критериев поиска придется снова прогонять в интерфейсе,
    // поэтому мне показалось проще оставить получение криетриев поиска, взаимодествие с интерфейсом и проверку оставить в одном методе
    //Суть проверки по шагам:
    // 1. Открыть меню и получить список критериев поиска
    // 2. Получить критерии поиска по заголовкам таблицы
    // 3. Сравнить криетрий из меню с криетрием из заголовка таблицы
    // 4. Если совпали! классно - берем первый непустой критерий из таблицы и осуществляем поиск
    // ...5. Иначе, в левом меню отображаются варианты значений критериев, взять их...
    // Тут большой вопрос как проверить что количество сопадает?
    // Вариант 1 "тупо в лоб": поочередно открывая формы и ища необходимые даные - что фигово,
    // т.к. долго и надо учесть два момента:
    // 1) определять текстбокс под определенным лейблом - та еще задачка и
    // 2) учесть вариант когда надо осуществлять перход на другую страницу дял поиска
    // 6. Считать всего в таблице - учесть перход на вторую страницу
    // 7. Написать два варианта проверок позитивные тесты и негативные

    public void search( ) {
        login( );
        getCriteriaFromMenu(  );
        softAssert.assertAll( );
    }

    public void getCriteriaFromMenu( ) {
        menu.click( );
        //System.out.println(menu.getText() );
        //Цикл по элементам меню
        for (int i = 0; i < menuPoint.size( ); i++) {
            //Ждем появления подменю
            //waitSomeMillisec( 1000 );
            //Если подменю не появилось, т.е. текст пункта пуст, то еще раз нажмем на меню
            if (menuPoint.get( i ).getText( ).isEmpty( )) {
                menu.click( );
                //waitSomeMillisec( 1000 );
            }
            //прокрутить до конца выпадающего меню, чтобы получить самый последний элемент
            WebElement target = menuPoint.get( i );
            ( (JavascriptExecutor) wd ).executeScript( "arguments[0].scrollIntoView(true);", target );
            //Нажать на выбранный пункт меню
            target.click( );
            //System.out.println( "after click menu.getText( )=" + menu.getText( ) );
            //Получить название пункта подменю
            String menuPointText = menuPoint.get( i ).getText( );
            waitSomeMillisec( 500 );
            //Цикл по заголовкам таблицы
            for (int j = 0; j < tableHeader.size( ); j++) {
                //Переменная поиска текста для вставки в строку поиска
                String criteriaText = "";
                //Если есть заголовок меню = заголовку таблицы
                if (!tableHeader.get( j ).getText( ).isEmpty( ) & menuPointText.equals( tableHeader.get( j ).getText( ) )) {
                    //System.out.println( "table header " + tableHeader.get( j ).getText( ) );
                    //Ищем текст для вставки в строку поиска
                    criteriaText = getNotNullValueFromColumn( j );
                    int countBeforeSearch = countByCriterion( criteriaText, j );
                    //тогда вставляем текст в строку поиска
                    searchAreaTB.click( );
                    searchAreaTB.clear( );
                    searchAreaTB.sendKeys( criteriaText );
                    //System.out.println( "Size " + tableRow.size( ) );
                    waitSomeMillisec( 500 );
                    int countAfterSearch = tableRow.size( );
                    softAssert.assertEquals( countBeforeSearch, countAfterSearch );
                    searchAreaTB.click( );
                    searchAreaTB.clear( );
                }
            }
        }
    }

    public String getNotNullValueFromColumn( int numElement ) {
        for (int i = numElement; i < tableCol.size( ); i += tableHeader.size( )) {
            //проанализировать массив и взять каждый житый ненулевой элемент
            System.out.println( "From table " + tableCol.get( i ).getText( ) );
            if (!tableCol.get( i ).getText( ).isEmpty( )) {
                return tableCol.get( i ).getText( );
            }
            //Иначе проверить есть ли пагинация и если есть перейти на вторую страницу и попытаться поискать там
            else {
                if (pagginationArrow.getAttribute( "aria-disabled" ) != null) {
                    while (pagginationArrow.getAttribute( "aria-disabled" ).equals( "false" )) {
                        pagginationArrow.click( );
                        if (!tableCol.get( i ).getText( ).isEmpty( )) {
                            return tableCol.get( i ).getText( );
                        }
                    }
                }
            }
        }
        return "";
    }

    private int countByCriterion( String searchCriterion, int startJ ) {
        int k = 0;
        for (int j = startJ; j < tableCol.size( ); j = j + tableHeader.size( )) {
            if (tableCol.get( j ).getText( ).contains( searchCriterion )) {
                k++;
            }
        }
        return k;
    }
}
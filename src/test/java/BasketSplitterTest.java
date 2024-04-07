
import com.ocado.basket.BasketSplitter;
import com.ocado.helperclass.JsonLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import java.io.IOException;
import java.util.*;

class BasketSplitterTest {
    private BasketSplitter splitter;

    @BeforeEach
    void setUp() throws IOException {
        splitter = new BasketSplitter("src\\main\\resources\\config.json");
    }

    @Test
    void testSplitWithBasket1() throws IOException {
        List<String> items = JsonLoader.loadItems("src/test/java/resources/basket-1.json");

        Map<String, List<String>> expectedDeliveryGroups = new HashMap<>();
        expectedDeliveryGroups.put("Pick-up point", List.of("Fond - Chocolate"));
        expectedDeliveryGroups.put("Courier", Arrays.asList("Cocoa Butter", "Tart - Raisin And Pecan", "Table Cloth 54x72 White", "Flower - Daisies", "Cookies - Englishbay Wht"));

        Map<String, List<String>> actualDeliveryGroups = splitter.split(items);
        expectedDeliveryGroups.forEach((method, products) -> assertThat(actualDeliveryGroups, hasEntry(equalTo(method), containsInAnyOrder(products.toArray()))));
    }

    @Test
    void testSplitWithBasket2() throws IOException {
        List<String> items = JsonLoader.loadItems("src/test/java/resources/basket-2.json");

        Map<String, List<String>> expectedDeliveryGroups = new HashMap<>();
        expectedDeliveryGroups.put("Same day delivery", Arrays.asList("Sauce - Mint", "Numi - Assorted Teas", "Garlic - Peeled"));
        expectedDeliveryGroups.put("Courier", List.of("Cake - Miini Cheesecake Cherry"));
        expectedDeliveryGroups.put("Express Collection", Arrays.asList(
                "Fond - Chocolate", "Chocolate - Unsweetened", "Nut - Almond, Blanched, Whole", "Haggis",
                "Mushroom - Porcini Frozen", "Longan", "Bag Clear 10 Lb", "Nantucket - Pomegranate Pear",
                "Puree - Strawberry", "Apples - Spartan", "Cabbage - Nappa", "Bagel - Whole White Sesame", "Tea - Apple Green Tea"));

        Map<String, List<String>> actualDeliveryGroups = splitter.split(items);

        expectedDeliveryGroups.forEach((method, products) -> assertThat(actualDeliveryGroups, hasEntry(equalTo(method), containsInAnyOrder(products.toArray()))));
    }
    @Test
    void TestLargestDeliveryNotOptimal() {
        List<String> items = Arrays.asList("produkt1", "produkt2", "produkt3","produkt4", "produkt5","produkt6");
        Map<String, List<String>> expectedGroups = new HashMap<>();
        expectedGroups.put("metoda2", Arrays.asList("produkt1", "produkt2", "produkt6"));
        expectedGroups.put("metoda3", Arrays.asList("produkt3", "produkt4", "produkt5"));

        Map<String, List<String>> actualGroups = splitter.split(items);

        expectedGroups.forEach((method, products) -> assertThat(actualGroups, hasEntry(equalTo(method), containsInAnyOrder(products.toArray()))));
    }
    @Test
    void testSingleProduct() {
        List<String> items = List.of("produkt6");
        Map<String, List<String>> expectedGroups = Map.of(
                "metoda5", List.of("produkt6")
        );
        Map<String, List<String>> actualGroups = splitter.split(items);
        expectedGroups.forEach((method, products) -> assertThat(actualGroups, hasEntry(equalTo(method), containsInAnyOrder(products.toArray()))));

    }
    @Test
    void EqualDeliveryMethod() {
        List<String> items = Arrays.asList("test1", "test2", "test3","test4", "test5","test6");
        Map<String, List<String>> expectedGroups = new HashMap<>();
        expectedGroups.put("metoda2", Arrays.asList("test1", "test2", "test6"));
        expectedGroups.put("metoda3", Arrays.asList("test3", "test4", "test5"));

        Map<String, List<String>> actualGroups = splitter.split(items);

        expectedGroups.forEach((method, products) -> assertThat(actualGroups, hasEntry(equalTo(method), containsInAnyOrder(products.toArray()))));

    }
    @Test
    void OneProductOneDelivery() {
        List<String> items = Arrays.asList("Cheese - Sheep Milk", "Chickhen - Chicken Phyllo","Cake - Miini Cheesecake Cherry");
        Map<String, List<String>> expectedGroups = new HashMap<>();
        expectedGroups.put("Mailbox delivery", List.of("Cheese - Sheep Milk"));
        expectedGroups.put("Pick-up point", List.of("Chickhen - Chicken Phyllo"));
        expectedGroups.put("Courier", List.of("Cake - Miini Cheesecake Cherry"));

        Map<String, List<String>> actualGroups = splitter.split(items);

        expectedGroups.forEach((method, products) -> assertThat(actualGroups, hasEntry(equalTo(method), containsInAnyOrder(products.toArray()))));

    }
}



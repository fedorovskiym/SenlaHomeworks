package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.product.ProductSearchRequest;
import com.senla.ProductService.service.AIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIServiceImpl implements AIService {
    //todo: добавь в реадме чуть больше инфы про данный функционал. Чуть подробнее расккажи, будет очень круто
    private final ChatClient chatClient;
    private static final String PROMPT = """
            Ты анализируешь запрос пользователя для поиска продуктов питания в интернет-магазине.
            
            Твоя задача — извлечь структурированные данные из запроса.
            
            ВАЖНЫЕ ПРАВИЛА:
            - Не выдумывай информацию
            - Используй только то, что явно указано в запросе
            - Если значение отсутствует — верни null
            - Не делай предположений
            
            ПОЛЯ:
            
            1. categoryName:
            - Общая категория продукта питания
            - Выбирай только из справочника категорий
            - Если неясно — null
            
            2. brandName:
            - Бренд продукта (если указан)
            - Выбирай только из справочника брендов
            - Если нет бренда — null
            
            3. productName:
            - Конкретный продукт или тип продукта
            - Более конкретно, чем categoryName
            
            4. description:
            - Относи сюда все, что не подошло в остальные параметры
            
            ОГРАНИЧЕНИЯ:
            - Не добавляй объяснения
            - Не расширяй запрос
            - Не придумывай бренды или продукты
            
            ПРИМЕРЫ:
            
            Запрос: "молоко 2.5%"
            Ответ:
            categoryName: Молочные продукты
            brandName: null
            productName: Молоко 2.5%
           
            Запрос: "куриная грудка"
            Ответ:
            categoryName: Мясо
            brandName: null
            productName: Куриная грудка
            
            СПРАВОЧНИК КАТЕГОРИЙ ТОВАРОВ:
            {CATEGORIES}
            
            
            СПРАВОЧНИК НАЗВАНИЙ БРЕНДОВ:
            {BRANDS}
            
            Запрос:
            """;

    @Autowired
    public AIServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public ProductSearchRequest getProductSearchRequest(String searchQuery, List<String> brandNames, List<String> categoryNames) {
        String query = PROMPT.replace("{CATEGORIES}", String.join("\n", categoryNames))
                .replace("{BRANDS}", String.join("\n", brandNames)).concat(searchQuery);
        return chatClient.prompt()
                .user(query)
                .call()
                .entity(ProductSearchRequest.class);
    }
}

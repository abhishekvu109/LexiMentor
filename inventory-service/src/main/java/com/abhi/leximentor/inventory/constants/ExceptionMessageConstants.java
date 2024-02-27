package com.abhi.leximentor.inventory.constants;

public class ExceptionMessageConstants {
    public static final String INVALID_DTO_REQUEST = String.format("The request has some part missing or invalid");

    public static class WordMetadata {
        public static final String DUPLICATE_WORD_FOUND = "An existing word found in the database with the same name";
        public static final String WORD_NOT_FOUND = "The word object doesn't exist in the database.";
    }

    public static class ProductType {
        public static final String DUPLICATE_PRODUCT_TYPE_FOUND = "An existing product type is found in the database with the same name";
        public static final String PRODUCT_TYPE_NOT_FOUND = "The product type doesn't exist in the database";
    }

    public static class Product {
        public static final String DUPLICATE_PRODUCT_TYPE_FOUND = "An existing product is found in the database with the same name";
        public static final String PRODUCT_NOT_FOUND = "The product doesn't exist in the database";
    }

    public static class Supplier {
        public static final String DUPLICATE_SUPPLIER_FOUND = "An existing supplier found in the database with the same name";
        public static final String SUPPLIER_NOT_FOUND = "The supplier object doesn't exist in the database.";
    }

    public static class Inventory {
        public static final String INVENTORY_OBJECT_NOT_FOUND = "The inventory object is not found";
    }
}

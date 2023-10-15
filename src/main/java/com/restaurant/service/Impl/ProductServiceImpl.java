package com.restaurant.service.Impl;

import com.restaurant.Entity.Category;
import com.restaurant.Entity.Product;
import com.restaurant.JWT.JwtFilter;
import com.restaurant.constents.RestaurantConstants;
import com.restaurant.repository.ProductRepository;
import com.restaurant.service.ProductService;
import com.restaurant.utils.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (ValidatedProductMap(requestMap, false)) {
                    productRepository.save(getProductFromMap(requestMap, false));
                    return RestaurantUtils.getResponseEntity("product Added Successfully.", HttpStatus.OK);
                }
                return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean ValidatedProductMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId){
                return true;
            }
            else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));

        Product product = new Product();
        if (isAdd) {
            product.setId(Integer.parseInt(requestMap.get("id")));
        } else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }
}

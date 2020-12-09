package com.safesoft.safemobile.backend.api.response

import com.safesoft.safemobile.backend.db.local.entity.Products

class ProductReponse(status: Boolean, message: String, product: Products) :
    BaseResponse(status, message)

class ProductsResponse(status: Boolean, message: String, val products: List<Products>) :
    BaseResponse(status, message)
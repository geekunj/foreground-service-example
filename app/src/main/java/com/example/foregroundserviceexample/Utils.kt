package com.example.foregroundserviceexample

import com.jakewharton.rxrelay3.PublishRelay

val RxBus by lazy { PublishRelay.create<Boolean>() }
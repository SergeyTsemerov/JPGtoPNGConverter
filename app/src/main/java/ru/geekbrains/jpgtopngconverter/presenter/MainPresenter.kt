package ru.geekbrains.jpgtopngconverter.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import ru.geekbrains.jpgtopngconverter.model.ConverterModel
import ru.geekbrains.jpgtopngconverter.view.MainView
import java.lang.Thread.sleep

class MainPresenter : MvpPresenter<MainView>() {

    private val model = ConverterModel()
    private lateinit var disposable: Disposable

    fun setContext(context: Context) {
        model.context = context
    }

    fun saveJPGImageAsPNG(data: Intent) {
        val observable = Observable.just(data)
        disposable = observable
            .subscribeOn(Schedulers.newThread())
            .map {
                sleep(2000)
                model.getBitmap(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Bitmap>() {
                override fun onNext(bitmap: Bitmap) {
                    model.saveConvertedFileInStorage(bitmap)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onComplete() {
                    viewState.directoryPath(model.savedPNGFile.absolutePath)
                }
            })
    }
}
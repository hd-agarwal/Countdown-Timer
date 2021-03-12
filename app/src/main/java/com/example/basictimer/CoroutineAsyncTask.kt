//since google is depreciating the async task this is a replacement for it using coroutines.
//It isnt complete as it lacks the cancel functionality and a lot of error checks and memory leaks, but it is working fine for me

package com.example.basictimer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class CoroutineAsyncTask<Params,Progress,Result> {
    fun execute(vararg params:Params?)
    {
        GlobalScope.launch(Dispatchers.Main){
            onPreExecute()
        }
        GlobalScope.launch(Dispatchers.Default){
            val res=doInBackground(*params)
            GlobalScope.launch(Dispatchers.Main) {
                onPostExecute(res)
            }
        }

    }

    abstract fun doInBackground(vararg params:Params?):Result
    open fun onPreExecute(){}
    open fun onPostExecute(result:Result?) {}
    open fun onProgressUpdate(vararg progress:Progress?) {}
    open fun publishProgress(vararg progress:Progress?)
    {
        GlobalScope.launch(Dispatchers.Main){
            onProgressUpdate(*progress)
        }
    }
    open fun onCancelled(result:Result?) {}
    fun cancel(mayInterruptIfRunning:Boolean)
    {

    }

}
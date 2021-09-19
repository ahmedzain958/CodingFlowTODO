package com.zainco.codingflowtodo.data

import android.net.wifi.p2p.WifiP2pManager
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zainco.codingflowtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    //this could be responsible for creating dummy database records for user first time displaying
    class Callback @Inject/*@Inject tells dagge how to create an instance of this callback class*/ constructor(
        private val database: Provider<TaskDatabase>,/*lazily initialized using provider*/
        @ApplicationScope private val applicationScope: CoroutineScope,//what if we wanted to create another CoroutineScope that will be used in another portion in the app
    //so here Callback class doesn't know which scope is the intended one to be provided from the module class, so we must create a qualifier annotation @ApplicationScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {//method is executed the first time we open this database, not everytime we start the app
            super.onCreate(db)
            //db opertation
            val dao = database.get().taskDao()
            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Do the laundry"))
                dao.insert(Task("Buy groceries", important = true))
                dao.insert(Task("Prepare food", completed = true))
                dao.insert(Task("Call mom"))
                dao.insert(Task("Visit grandma", completed = true))
                dao.insert(Task("Repair my bike"))
                dao.insert(Task("Call Elon Musk"))
            }
        }

    }

}
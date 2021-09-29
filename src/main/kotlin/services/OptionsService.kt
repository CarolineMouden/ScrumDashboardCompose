package services

import fr.gmo.scrumboard.model.Options
import org.kodein.db.DB
import org.kodein.db.useModels

interface OptionsAction {
    fun disconnect()
    fun update(options: Options)
}

class OptionsService(val db: DB, val onUpdate:(Options) -> Unit) {
    init {
        val allOptions = db.find(Options::class).all().useModels() {it.toList()}
        if (allOptions.isNotEmpty()) {
            onUpdate(allOptions[0])
        }
        db.on(Options::class).register {
            didPut {
                val allOptions = db.find(Options::class).all().useModels() {it.toList()}
                if (allOptions.isNotEmpty()) {
                    onUpdate(allOptions[0])
                }
            }
        }
    }

    fun updateItem(options: Options, onUpdate: (Options) -> Unit) {
        db.put(options)
        onUpdate(options)
    }

}

data class GetResponse(val truc:String)
package services

import fr.gmo.scrumboard.model.*
import org.kodein.db.DB
import org.kodein.db.getById
import org.kodein.db.useModels
import java.util.*
import kotlin.collections.ArrayList

interface IssueAction {
    fun changeScreen(screen: String)
    fun get(screen: String): Issue
    fun update(Issue: Issue)
    fun delete(Issue: Issue)
}

class IssueService(val db: DB, val onUpdate:(List<Issue>)->Unit) {
    init {
        onUpdate(db.find(Issue::class).all().useModels() {it.toList()})
        db.on(Issue::class).register {
            didDeleteIt {
                onUpdate(db.find(Issue::class).all().useModels() {it.toList()})
            }
            didPut {
                onUpdate(db.find(Issue::class).all().useModels() {it.toList()})
            }
        }
    }

    fun getIssue(id: String?): Issue {
        if (id == null) {
            return Issue()
        } else {
            return db.getById(id)?: Issue()
        }
    }

    fun updateItem(issue: Issue, onUpdate: () -> Unit) {
        if (issue.id == null) {
            issue.id = UUID.randomUUID().toString()
        }
        db.put(issue)
        onUpdate()
    }

    fun deleteItem(issue: Issue, onDelete: () -> Unit) {
        issue.id?.let {
            db.delete(Issue::class, db.keyById(Issue::class, it))
            onDelete()
        }
    }

}
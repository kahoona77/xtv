package de.kahoona.xtv.db

import com.gmongo.GMongo
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.WriteResult
import org.bson.types.ObjectId

/**
 * Created by Benni on 19.04.2014.
 */
@SuppressWarnings("GroovyAssignabilityCheck")
class MongoDB {

    DB db

    public static MongoDB newInstance () {
        return new MongoDB();
    }

    public MongoDB () {
        def gmongo = new GMongo("localhost:27017")
        this.db = gmongo.getDB("xtv")
    }

    public List findAll (String collectionName) {
        DBCollection collection = this.db[collectionName] as DBCollection
        DBCursor cursor = collection.find()
        List result = []
        cursor.each {
            result << it
        }

        return result
    }

    List findWithQuery(String collectionName, Map query, int limit) {
        DBCollection collection = this.db[collectionName] as DBCollection
        DBCursor cursor = collection.find(query).limit (limit)
        List result = []
        cursor.each {
            result << it
        }

        return result
    }

    public def findById (String collectionName, String id) {
        DBCollection collection = this.db[collectionName] as DBCollection
        def result = collection.findOne(['_id': id])
        return result
    }

    public def findFirst (String collectionName) {
        DBCollection collection = this.db[collectionName] as DBCollection
        def result = collection.findOne()
        return result
    }

    public def save (String collectionName, def document) {
        if (!document._id) {
            document._id =  new ObjectId().toString()
        }
        DBCollection collection = this.db[collectionName] as DBCollection
        WriteResult result = collection.update(['_id': document._id], document, true, false)
        return result
    }

    public def remove (String collectionName, def document) {
        DBCollection collection = this.db[collectionName] as DBCollection
        WriteResult result = collection.remove(['_id': document._id])
        return result
    }

    long countAll(String collectionName) {
        DBCollection collection = this.db[collectionName] as DBCollection
        return collection.count()
    }


}

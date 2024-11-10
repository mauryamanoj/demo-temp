// removeDotHtmlFromLinks.groovy
// Remove .html from links

//inputs to copy/past on Data
/*{
   "commitChange": false,
   "path": "/content/sauditourism/en",
   "from": "/content/sauditourism/"
}*/

//Input path, property and value to edit
def commitChange = data.commitChange
def path = data.path
def from = data.from

if (!from.startsWith("/content/sauditourism/")) {
    println "From must start with '/content/sauditourism/'";
    return;
}

def query = createSQL2Query(path, from)
def result = query.execute()

//Create Query
def createSQL2Query(path, from) {
    def queryManager = session.workspace.queryManager
    def statement = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([${path}]) and CONTAINS(s.*, '${from}%')"
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    query
}

//Output
println "Searching for nodes having a property containing " + from
if (result.nodes) {
    println "Number of nodes found : " + result.nodes.size()
} else {
    println "No Pages found"
    return
}

def searchAndRemoveHtml(value, from) {
    if (value.startsWith(from) && value.endsWith(".html")) {
        return value.replace(".html", "");
    }
    return null;
}

def data = []
result.nodes.each { node ->
    node.properties.each {
        prop ->
            def name = prop.getName()
            if (prop.getType() == javax.jcr.PropertyType.STRING) {
                def oldValue = null
                def newValue = null
                if (prop.isMultiple()) {
                    oldValue = prop.getValues()
                    oldValueStr = []
                    newValue = []
                    def changed = false
                    oldValue.each { item ->
                        def oldV = item.getString()
                        oldValueStr.add(oldV)
                        def newV = searchAndRemoveHtml(oldV, from)
                        if (newV != null) {
                            changed = true
                            newValue.add(newV)
                        } else {
                            newValue.add(oldV)
                        }
                    }
                    if (changed) {
                        node.set(name, newValue)
                    } else {
                        newValue = null
                    }
                    oldValue = oldValueStr
                } else {
                    oldValue = prop.getValue().getString()
                    newValue = searchAndRemoveHtml(oldValue, from)
                    if (newValue != null) {
                        node.set(name, newValue)
                    }
                }
                if (newValue != null) {
                    data.add([node.path, name, oldValue, newValue])
                }
            }
    }
}

// Save this session if you are sure to commit change.
if (commitChange) {
    session.save()
    println "Session Save : Update Success"
} else {
    println "Session Save : Not saved, as requested. Set commitChange to true if you want to commit"
}

table {
    columns("Component Path", "PropertyName", "From", "To")
    rows(data)
}
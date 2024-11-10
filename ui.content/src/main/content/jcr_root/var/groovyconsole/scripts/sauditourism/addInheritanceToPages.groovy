// addInheritanceToNode.groovy
// Add jcr:mixinTypes for node

/*
{
   "commitChange": false,
   "path": "/content/sauditourism/ar"
}
*/

// Input path, property and value to edit
def commitChange = data.commitChange
def path = data.path

// Create Query
def createSQL2Query(path) {
    def queryManager = session.workspace.queryManager
    def statement = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE([${path}])"
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    query
}

def data = []

def query = createSQL2Query(path)
def result = query.execute()

println "Processing nodes under path: ${path}"
if(result.nodes){
    println "Number of parent nodes found : "+result.nodes.size()
} else {
    println "No parent nodes found under ${path}"
    return
}

def processNode(node, data) {
    if (!node.hasProperty("jcr:mixinTypes") || !node.getProperty("jcr:mixinTypes").toString().contains("cq:LiveRelationship")) {
        node.addMixin("cq:LiveRelationship")
        data.add([node.path, "cq:LiveRelationship added"])
    }
    node.getNodes().each { childNode ->
        processNode(childNode, data)
    }
}

result.nodes.each { node ->
    if(session.nodeExists(node.path + "/jcr:content")){
        def jcrNode = session.getNode(node.path + "/jcr:content")
        processNode(jcrNode, data)
    }
}

// Save this session if you are sure to commit change.
if(commitChange){
    session.save()
    println "Session Save : Update Successful"
} else {
    println "Echec Update : change value of commitChange to true and re-execute to save session"
}

table {
    columns("Node Path", "Change")
    rows(data)
}

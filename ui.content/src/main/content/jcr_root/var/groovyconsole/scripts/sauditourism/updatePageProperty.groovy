// updatePageProperty.groovy
// Find all pages by template

//inputs to copy/past on Data
/*{
   "commitChange": false,
   "path": "/content/sauditourism/",
   "cqTemplate":"cq:template",
   "template":"/conf/sauditourism/settings/wcm/templates/destination-page",
   "propertyName": "searchHighlight",
   "propertyValue": "true"
}*/

//Input path, template and cqTemplate
def commitChange = data.commitChange
def path = data.path
def cqTemplate = data.cqTemplate
def template =  data.template
def propertyName = data.propertyName
def propertyValue = data.propertyValue

def query = createSQL2Query(path,  template , cqTemplate)
def result = query.execute()

//Create Query
def createSQL2Query(path, term, cqTemplate) {
    def queryManager = session.workspace.queryManager
    def statement = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE([${path}]) and s.[${cqTemplate}] = '${term}' "
    def query = queryManager.createQuery(statement, "JCR-SQL2")
    query
}

//Output
println "Search used "+cqTemplate+" with value : "+template
if(result.nodes){
    println "Number of Pages found : "+result.nodes.size()
} else {
    println "No Pages found"
    return
}

def data = []
result.nodes.each{node ->
    def oldValue = node.get(propertyName)
    node.set(propertyName, propertyValue)
    data.add([node.path.substring(0, node.path.indexOf("/jcr:content")), oldValue, node.get(propertyName)])
}

// Save this session if you are sure to commit change.
if(commitChange){
    session.save()
    println "Session Save : Update Success"
} else {
    println "Failed Update : change value of commitChange to true and re-execute to save session"
}

table {
    columns("Page Path", propertyName + " old value", propertyName + " new value")
    rows(data)
}
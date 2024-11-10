// searchPagesByTemplate.groovy
// Find all pages by template

//inputs to copy/past on Data
/*{
   "path": "/content/sauditourism/en",
   "cqTemplate":"cq:template",
   "template":"/conf/sauditourism/settings/wcm/templates/content-page"
}*/

//Input path, template and cqTemplate
def path = data.path
def cqTemplate = data.cqTemplate
def template =  data.template

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
    data.add([node.path.substring(0, node.path.indexOf("/jcr:content")), template.substring(template.lastIndexOf("/") + 1)])
}

table {
    columns("Page Path", "Template")
    rows(data)
}
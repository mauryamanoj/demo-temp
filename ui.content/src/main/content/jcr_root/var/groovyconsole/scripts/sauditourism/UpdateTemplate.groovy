// UpdateTemplate.groovy
// Update Template

//inputs to copy/past on Data
/*
{
   "commitChange": false,
   "path": "/content/sauditourism/en",
   "cqTemplate": "cq:template",
   "template": "/conf/sauditourism/settings/wcm/templates/content-page",
   "newTemplate": "/conf/sauditourism/settings/wcm/templates/base-content-page",
   "resourceType": "sling:resourceType",
   'newResourceType": "sauditourism/components/structure/base-content-page"
   }
*/

//Input
def commitChange = data.commitChange
def path = data.path
def cqTemplate =  data.cqTemplate
def template =  data.template
def newTemplate =  data.newTemplate
def resourceType = data.resourceType
def newResourceType = data.newResourceType

def query = createSQL2Query(path, template , cqTemplate)
def result = query.execute()

//Create Query
def createSQL2Query(path, term, cqTemplate) {
  def queryManager = session.workspace.queryManager
  def statement = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE([${path}]) and s.[${cqTemplate}] = '${term}' "
  // To Search on single page =>
  // def statement = "SELECT * FROM [cq:PageContent] AS s WHERE [jcr:path] = '${path}/jcr:content' and s.[${cqTemplate}] = '${term}' "
  def query = queryManager.createQuery(statement, "JCR-SQL2")
  query
}

//Output
def data = []
result.nodes.each{node ->
    node.set(cqTemplate, newTemplate)
    node.set(resourceType, newResourceType)
    data.add([node.path.substring(0, node.path.indexOf("/jcr:content")), template.substring(template.lastIndexOf("/") + 1), node.get(cqTemplate).substring(node.get(cqTemplate).lastIndexOf("/") + 1)])
}
// Save this session if you are sure to commit change.
if(commitChange){
  session.save()
  println "Session Save : Update Succes"
} else {
  println "Echec Update : change value of commitChange to true and re-execute to save session"
}

table {
  columns("Page Path", "Old "+cqTemplate, "New "+cqTemplate)
  rows(data)
}

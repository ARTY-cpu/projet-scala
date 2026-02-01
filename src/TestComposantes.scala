/**
 * Programme de test pour les composantes fortement connexes
 * et les distributions stationnaires par classe
 */
object TestComposantes extends App {
  
  println("=" * 70)
  println("TEST : COMPOSANTES FORTEMENT CONNEXES ET DISTRIBUTIONS")
  println("=" * 70)
  
  val exemples = List(
    ("exemples/exemple1.txt", "Exemple 1 : Graphe simple 4 sommets"),
    ("exemples/exemple2.txt", "Exemple 2 : Graphe 10 sommets (validation step 3)"),
    ("exemples/exemple3.txt", "Exemple 3 : Graphe complexe 8 sommets"),
    ("exemples/exemple_meteo.txt", "Exemple Météo : 5 états"),
    ("exemples/exemple_valid_step3.txt", "Exemple validation : 10 sommets")
  )
  
  for ((fichier, description) <- exemples) {
    println("\n" + "=" * 70)
    println(description)
    println(s"Fichier: $fichier")
    println("=" * 70)
    
    Chargeur.charger(fichier) match {
      case Some(graphe) =>
        println(s" Graphe chargé: ${graphe.nbSommets} sommets\n")
        
        // Analyse complète : Tarjan + distributions
        val partition = ComposantesFortementConnexes.analyseComplete(graphe)
        
        // Statistiques
        println("\n>>> STATISTIQUES")
        println(s"  Nombre de composantes: ${partition.nbComposantes}")
        
        val matrice = graphe match {
          case m: MatriceAdjacence => m
          case l: ListeAdjacence => CalculsMatriciels.listeVersMatrice(l)
        }
        
        val nbTransitoires = partition.composantes.count { classe =>
          ComposantesFortementConnexes.typeClasse(graphe, classe) == Transitoire
        }
        val nbPersistantes = partition.nbComposantes - nbTransitoires
        
        println(s"  Classes transitoires: $nbTransitoires")
        println(s"  Classes persistantes: $nbPersistantes")
        
        // Classification du graphe
        if (partition.nbComposantes == 1) {
          println(s"  Type de graphe: IRRÉDUCTIBLE (1 seule classe)")
        } else {
          println(s"  Type de graphe: NON IRRÉDUCTIBLE (${partition.nbComposantes} classes)")
        }
        
      case None =>
        println(s" Échec du chargement de $fichier")
    }
    
    println("\n" + "=" * 70)
  }
  
  // Test spécifique détaillé pour exemple2 (le graphe du diagramme)
  println("\n\n" + "=" * 70)
  println("TEST DÉTAILLÉ : Exemple 2 (graphe de validation)")
  println("=" * 70)
  
  Chargeur.charger("exemples/exemple2.txt") match {
    case Some(graphe) =>
      val partition = ComposantesFortementConnexes.tarjan(graphe)
      
      println("\nVérification avec les résultats attendus:")
      println("Attendu:")
      println("  - Composante C1: {1,5,7}")
      println("  - Composante C2: {2}")
      println("  - Composante C3: {3,6,8}")
      println("  - Composante C4: {4}")
      println("  - Composante C5: {9}")
      println("  - Composante C6: {10}")
      
      println("\nObtenu:")
      partition.composantes.zipWithIndex.foreach { case (comp, idx) =>
        val sommets = comp.toList.sorted.mkString(", ")
        println(f"  - Composante C${idx + 1}: {$sommets}")
      }
      
      println("\n Test de conformité avec l'énoncé")
      
    case None =>
      println(" Échec du chargement")
  }
  
  println("\n" + "=" * 70)
  println("FIN DES TESTS")
  println("=" * 70)
}

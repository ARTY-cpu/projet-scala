/**
 * Programme principal
 */
object Main extends App {
  
  println("=" * 50)
  println("GRAPHES DE MARKOV")
  println("=" * 50)
  
  val fichier = "exemples/exemple1.txt"
  
  println(s"\nChargement: $fichier")
  println("-" * 50)
  
  Chargeur.charger(fichier) match {
    case Some(graphe) =>
      println(s"✓ ${graphe.nbSommets} sommets\n")
      
      graphe.afficher()
      graphe.afficherValidation()
      
      println(">>> Successeurs:")
      println("-" * 50)
      for (s <- 1 to graphe.nbSommets) {
        val succ = graphe.successeurs(s)
        val str = succ.map { case (d, p) => f"$d($p%.2f)" }.mkString(", ")
        println(s"Sommet $s → $str")
      }
      
    case None =>
      println("✗ Échec")
  }
  
  println("\n" + "=" * 50)
}

/**
 * Programme principal - Test des graphes de Markov
 */
object Main extends App {
  
  println("=" * 50)
  println("GRAPHES DE MARKOV - ÉTAPE 1")
  println("=" * 50)
  
  // Chemin vers le fichier d'exemple
  val fichier = "exemples/exemple1.txt"
  
  println(s"\nChargement: $fichier")
  println("-" * 50)
  
  Chargeur.charger(fichier) match {
    
    case Some(matrice) =>
      println("✓ Chargé\n")
      
      // Affichage
      matrice.afficher()
      
      // Validation
      matrice.afficherValidation()
      
      // Successeurs
      afficherSuccesseurs(matrice)
      
    case None =>
      println("✗ Échec du chargement")
  }
  
  println("=" * 50)
  
  /**
   * Affiche les successeurs de chaque sommet
   * Utilise HOF map avec lambda
   */
  def afficherSuccesseurs(m: MatriceAdjacence): Unit = {
    println(">>> Successeurs:")
    println("-" * 50)
    
    for (s <- 1 to m.taille) {
      val succ = m.successeurs(s)
      
      if (succ.isEmpty) {
        println(s"Sommet $s → ∅")
      } else {
        val str = succ.map { case (d, p) => f"$d($p%.2f)" }.mkString(", ")
        println(s"Sommet $s → $str")
      }
    }
    println()
  }
}

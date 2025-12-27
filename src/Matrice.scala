/**
 * Matrice d'adjacence pour un graphe de Markov
 * 
 * Un graphe de Markov valide a la propriété suivante :
 * - La somme des probabilités sortantes de chaque sommet = 1
 * 
 * @param n nombre de sommets
 */
class MatriceAdjacence(val n: Int) extends Matrice[Double] {
  
  private val matrice: Array[Array[Double]] = Array.ofDim[Double](n, n)
  
  // Initialisation à 0
  for (i <- 0 until n; j <- 0 until n) matrice(i)(j) = 0.0
  
  /**
   * Récupère la probabilité de transition i → j
   * @param i sommet de départ (base 1)
   * @param j sommet d'arrivée (base 1)
   */
  def get(i: Int, j: Int): Double = {
    require(estValide(i) && estValide(j), s"Indices invalides: ($i, $j)")
    matrice(i - 1)(j - 1)
  }
  
  /**
   * Définit la probabilité de transition i → j
   * @param i sommet de départ (base 1)
   * @param j sommet d'arrivée (base 1)
   * @param valeur probabilité [0, 1]
   */
  def set(i: Int, j: Int, valeur: Double): Unit = {
    require(estValide(i) && estValide(j), s"Indices invalides: ($i, $j)")
    require(valeur >= 0 && valeur <= 1, s"Probabilité invalide: $valeur")
    matrice(i - 1)(j - 1) = valeur
  }
  
  def taille: Int = n
  
  /** Vérifie si un indice de sommet est valide */
  private def estValide(sommet: Int): Boolean = sommet >= 1 && sommet <= n
  
  /** Affiche la matrice formatée */
  def afficher(): Unit = {
    println(s"\nMatrice ${n}x${n}:")
    println("    " + (1 to n).map(i => f"$i%6d").mkString(" "))
    println("    " + "-" * (7 * n))
    
    for (i <- 0 until n) {
      print(f"${i + 1}%3d |")
      for (j <- 0 until n) {
        print(f"${matrice(i)(j)}%6.2f ")
      }
      println()
    }
    println()
  }
  
  /**
   * Calcule la somme des probabilités sortantes d'un sommet
   * Utilise HOF map et sum
   */
  def sommeSortante(sommet: Int): Double = {
    require(estValide(sommet), s"Sommet invalide: $sommet")
    (1 to n).map(j => get(sommet, j)).sum
  }
  
  /**
   * Vérifie si c'est un graphe de Markov valide
   * Utilise HOF forall avec lambda
   */
  def estValide(epsilon: Double = 0.0001): Boolean = {
    (1 to n).forall { sommet =>
      val somme = sommeSortante(sommet)
      math.abs(somme - 1.0) < epsilon || somme == 0.0
    }
  }
  
  /** Affiche le rapport de validation */
  def afficherValidation(epsilon: Double = 0.0001): Unit = {
    println("\n=== Validation du graphe de Markov ===")
    
    var valide = true
    for (sommet <- 1 to n) {
      val somme = sommeSortante(sommet)
      if (somme > 0) {
        val ok = math.abs(somme - 1.0) < epsilon
        val statut = if (ok) "✓" else "✗"
        println(f"Sommet $sommet%2d : somme = $somme%.4f $statut")
        if (!ok) valide = false
      }
    }
    
    println("\n" + (if (valide) "✓ VALIDE" else "✗ INVALIDE"))
    println("=" * 40 + "\n")
  }
  
  /**
   * Retourne les successeurs d'un sommet
   * Utilise HOF map et filter avec lambda
   */
  def successeurs(sommet: Int): List[(Int, Double)] = {
    require(estValide(sommet), s"Sommet invalide: $sommet")
    (1 to n)
      .map(j => (j, get(sommet, j)))
      .filter(_._2 > 0)
      .toList
  }
}

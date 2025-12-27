/**
 * Trait générique pour représenter une matrice
 * @tparam T type des éléments de la matrice
 */
trait Matrice[T] {
  def get(i: Int, j: Int): T
  def set(i: Int, j: Int, valeur: T): Unit
  def taille: Int
  def afficher(): Unit
}

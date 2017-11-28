package http

trait Http {
  def execute(request: Request): Response
}

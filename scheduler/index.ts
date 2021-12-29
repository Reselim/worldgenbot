import { scheduleJob } from "node-schedule"
import { spawn } from "child_process"
import { resolve } from "path"
import pino from "pino"

const logger = pino({
	transport: {
		target: "pino-pretty",
		options: {},
	},
})

function getGradleWrapper(): string {
	if (process.platform == "win32") {
		return resolve(__dirname, "../gradlew.bat")
	} else {
		return resolve(__dirname, "../gradlew")
	}
}

function processChunk(chunk: any): string {
	const data = chunk.toString() as string

	if (data.endsWith("\n")) {
		return data.slice(0, -1)
	} else {
		return data
	}
}

scheduleJob("run", "0 */2 * * * *", () => {
	const child = spawn(getGradleWrapper(), [ "runClient" ], {
		cwd: resolve(__dirname, ".."),
	})

	child.stdout.on("data", (chunk) => {
		logger.info(processChunk(chunk))
	})

	child.stderr.on("data", (chunk) => {
		logger.error(processChunk(chunk))
	})

	child.on("close", () => {
		logger.info("Child process exited")
	})
})
package raio.batch.core

import raio.batch.core.exception.BatchRunException
import raio.batch.core.factory.JobParametersFactory
import raio.batch.core.runner.SpringBatchJobRunner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException

class SpringBatchJobRunnerFreeSpecTest : FreeSpec({

    lateinit var jobRegistry: JobRegistry
    lateinit var jobLauncher: JobLauncher
    lateinit var job: Job
    lateinit var jobParametersFactory: JobParametersFactory
    lateinit var runner: SpringBatchJobRunner

    beforeTest {
        jobRegistry = mockk()
        jobLauncher = mockk()
        job = mockk()
        jobParametersFactory = JobParametersFactory()
        runner = SpringBatchJobRunner(jobRegistry, jobLauncher, jobParametersFactory)
    }

    "스프링배치 테스트(SpringBatchJobRunner)" - {
        "[정상] 실행 시 JobRegistry에서 잡을 찾아 JobLauncher로 실행한다" {
            // given
            val jobName = "productPlanGenerateJob"
            val params = mapOf(
                "userId" to "u-1",
                "batchId" to "b-1",
            )

            every { jobRegistry.getJob(jobName) } returns job

            val exec = mockk<JobExecution>()
            val capturedParams = slot<JobParameters>()
            every { jobLauncher.run(job, capture(capturedParams)) } returns exec

            // when
            val result = runner.run(jobName, params)

            // then
            result shouldBe exec

            verify(exactly = 1) { jobRegistry.getJob(jobName) }
            verify(exactly = 1) { jobLauncher.run(job, any()) }

            capturedParams.captured.getString("userId") shouldBe "u-1"
            capturedParams.captured.getString("batchId") shouldBe "b-1"
            capturedParams.captured.getLong("run.id").shouldNotBeNull()

            confirmVerified(jobRegistry, jobLauncher)
        }

        "jobName이 공백이면 IllegalArgumentException이 발생한다" {
            shouldThrow<IllegalArgumentException> {
                runner.run("   ", emptyMap())
            }

            verify { jobRegistry wasNot Called }
            verify { jobLauncher wasNot Called }
        }

        "이미 실행 중인 잡이면 BatchRunException으로 변환된다" {
            // given
            val jobName = "anyJob"

            every { jobRegistry.getJob(jobName) } returns job
            every { jobLauncher.run(job, any()) } throws JobExecutionAlreadyRunningException("already running")

            // when / then
            val ex = shouldThrow<BatchRunException> {
                runner.run(jobName, mapOf("k" to "v"))
            }
            ex.message!!.contains("Job already running") shouldBe true

            verify(exactly = 1) { jobRegistry.getJob(jobName) }
            verify(exactly = 1) { jobLauncher.run(job, any()) }
        }
    }
})
